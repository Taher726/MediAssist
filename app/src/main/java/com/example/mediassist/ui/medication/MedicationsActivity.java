package com.example.mediassist.ui.medication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.data.models.Medication;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MedicationsActivity extends AppCompatActivity {
    private static final String TAG = "MedicationsActivity"; // Add this for logging
    private LinearLayout medicationsContainer;
    private DatabaseHelper dbHelper;
    private FloatingActionButton addMedicationButton;
    private String userEmail;
    private UserSession userSession; // Add UserSession

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transparent nav bar and colored status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.activity_medications);

        // Get user session directly
        userSession = new UserSession(this);
        userEmail = userSession.getUserEmail();

        Log.d(TAG, "User email from session: " + userEmail); // Log the user email

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in. Please login first.", Toast.LENGTH_LONG).show();
            return;
        }

        initializeViews();
        setupNavigation();
        loadMedications();
        // Add this line after initializeViews();
        debugUserIds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedications(); // Reload medications when returning to this screen
    }

    private void initializeViews() {
        medicationsContainer = findViewById(R.id.medicationsContainer);
        addMedicationButton = findViewById(R.id.addMedicationButton);
        dbHelper = DatabaseHelper.getInstance(this);

        // Navigation icons
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView medicationIcon = findViewById(R.id.medicationIcon);
        ImageView renderVousIcon = findViewById(R.id.renderVousIcon);
        ImageView ordannanceIcon = findViewById(R.id.ordannanceIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);

        // Highlight current tab
        medicationIcon.setColorFilter(getResources().getColor(R.color.primary));
    }

    private void setupNavigation() {
        findViewById(R.id.homeIcon).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.renderVousIcon).setOnClickListener(v -> navigateTo(RendezVousActivity.class));
        findViewById(R.id.ordannanceIcon).setOnClickListener(v -> navigateTo(OrdonnancesActivity.class));
        findViewById(R.id.profileIcon).setOnClickListener(v -> navigateTo(ProfileActivity.class));

        addMedicationButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddMedicationActivity.class))
        );
    }

    private void navigateTo(Class<?> cls) {
        startActivity(new Intent(this, cls));
        finish();
    }

    private void loadMedications() {
        medicationsContainer.removeAllViews();

        Log.d(TAG, "Loading medications for user: " + userEmail); // Log before query

        List<Medication> medications = dbHelper.getMedicationsForUser(userEmail);

        Log.d(TAG, "Found " + medications.size() + " medications"); // Log the count

        if (medications.isEmpty()) {
            showEmptyState();
            return;
        }

        for (Medication medication : medications) {
            addMedicationCard(medication);
        }
    }

    private void showEmptyState() {
        TextView emptyView = new TextView(this);
        emptyView.setText("No medications found. Add your first medication!");
        emptyView.setTextSize(16);
        emptyView.setPadding(0, 32, 0, 0);
        emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        medicationsContainer.addView(emptyView);
    }

    private void addMedicationCard(Medication medication) {
        View cardView = getLayoutInflater().inflate(R.layout.medication_item, null);

        // Set layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Convert dp to pixels
        int marginInDp = 8;
        float density = getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (marginInDp * density);

        // Set margins
        layoutParams.setMargins(0, marginInPixels, 0, marginInPixels);
        cardView.setLayoutParams(layoutParams);

        TextView nameText = cardView.findViewById(R.id.medName);
        TextView typeText = cardView.findViewById(R.id.medType);
        TextView frequencyText = cardView.findViewById(R.id.medFrequency);
        TextView dosageText = cardView.findViewById(R.id.medDosage);
        TextView timesText = cardView.findViewById(R.id.medTimes);
        TextView daysText = cardView.findViewById(R.id.medDays);
        TextView notesText = cardView.findViewById(R.id.medNotes);
        TextView statusIndicator = cardView.findViewById(R.id.statusIndicator);
        final ImageView medIcon = cardView.findViewById(R.id.medIcon);
        ImageView deleteIcon = cardView.findViewById(R.id.deleteIcon);

        nameText.setText(medication.getName());
        typeText.setText("Type: " + medication.getType());
        frequencyText.setText("Frequency: " + medication.getFrequency());
        dosageText.setText("Dosage: " + medication.getDosage());
        timesText.setText("Times: " + medication.getFormattedTimes());
        daysText.setText("Days: " + medication.getFormattedDays());
        notesText.setText("Notes: " + medication.getNotes());
        statusIndicator.setText(medication.getStatus());

        int statusColor = medication.getStatus().equalsIgnoreCase("Active") ?
                getResources().getColor(R.color.active_green) :
                getResources().getColor(R.color.inactive_red);
        statusIndicator.setTextColor(statusColor);

        // Set default image first
        medIcon.setImageResource(R.drawable.medicine_placeholder);

        // Try loading image from path first
        String imagePath = medication.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imagePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                medIcon.setImageBitmap(bitmap);
                Log.d(TAG, "Loaded image from path: " + imagePath);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image from path: " + e.getMessage());

                // If path loading fails, try loading from database on a background thread
                new Thread(() -> {
                    final byte[] imageData = dbHelper.getMedicationImage(medication.getId());
                    if (imageData != null && imageData.length > 0) {
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                        // Update UI on main thread
                        runOnUiThread(() -> {
                            if (bitmap != null) {
                                medIcon.setImageBitmap(bitmap);
                                Log.d(TAG, "Loaded image from database for medication: " + medication.getName());
                            } else {
                                Log.e(TAG, "Failed to decode image from database");
                            }
                        });
                    } else {
                        Log.d(TAG, "No image data found in database for medication: " + medication.getName());
                    }
                }).start();
            }
        } else {
            // Try loading from database directly if no path is available
            new Thread(() -> {
                final byte[] imageData = dbHelper.getMedicationImage(medication.getId());
                if (imageData != null && imageData.length > 0) {
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                    // Update UI on main thread
                    runOnUiThread(() -> {
                        if (bitmap != null) {
                            medIcon.setImageBitmap(bitmap);
                            Log.d(TAG, "Loaded image from database for medication: " + medication.getName());
                        } else {
                            Log.e(TAG, "Failed to decode image from database");
                        }
                    });
                } else {
                    Log.d(TAG, "No image data found in database for medication: " + medication.getName());
                }
            }).start();
        }

        deleteIcon.setOnClickListener(v -> deleteMedication(medication.getId()));
        cardView.setOnClickListener(v -> editMedication(medication));

        medicationsContainer.addView(cardView);
    }

    private void deleteMedication(int medicationId) {
        boolean deleted = dbHelper.deleteMedication(medicationId);
        if (deleted) {
            Toast.makeText(this, "Medication deleted", Toast.LENGTH_SHORT).show();
            loadMedications();
        } else {
            Toast.makeText(this, "Failed to delete medication", Toast.LENGTH_SHORT).show();
        }
    }

    private void editMedication(Medication medication) {
        Intent intent = new Intent(this, AddMedicationActivity.class);
        intent.putExtra("medication_id", medication.getId());
        intent.putExtra("medication_name", medication.getName());
        intent.putExtra("medication_type", medication.getType());
        intent.putExtra("medication_frequency", medication.getFrequency());
        intent.putExtra("medication_dosage", medication.getDosage());
        intent.putExtra("medication_times", medication.getTime());
        intent.putExtra("medication_days", medication.getDays());
        intent.putExtra("medication_notes", medication.getNotes());
        intent.putExtra("medication_status", medication.getStatus());
        intent.putExtra("medication_image_path", medication.getImagePath());
        startActivity(intent);
    }

    // Add this method to MedicationsActivity
    private void debugUserIds() {
        try {
            // Get all users from database (just for debugging)
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, email FROM users", null);

            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "===== USERS IN DATABASE =====");
                do {
                    int id = cursor.getInt(0);
                    String email = cursor.getString(1);
                    Log.d(TAG, "User ID: " + id + ", Email: " + email);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                Log.d(TAG, "No users found in database");
            }

            // Get all medications
            Cursor medCursor = db.rawQuery("SELECT id, name, user_id FROM medications", null);
            if (medCursor != null && medCursor.moveToFirst()) {
                Log.d(TAG, "===== MEDICATIONS IN DATABASE =====");
                do {
                    int id = medCursor.getInt(0);
                    String name = medCursor.getString(1);
                    int userId = medCursor.getInt(2);
                    Log.d(TAG, "Med ID: " + id + ", Name: " + name + ", User ID: " + userId);
                } while (medCursor.moveToNext());
                medCursor.close();
            } else {
                Log.d(TAG, "No medications found in database");
            }

            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error debugging user IDs", e);
        }
    }
}