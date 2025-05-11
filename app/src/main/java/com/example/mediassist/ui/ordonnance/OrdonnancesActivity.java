package com.example.mediassist.ui.ordonnance;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.data.models.Prescription;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;
import com.example.mediassist.utils.FileHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdonnancesActivity extends AppCompatActivity {
    private static final String TAG = "OrdonnancesActivity";

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    private LinearLayout prescriptionsContainer;
    private EditText searchInput;
    private DatabaseHelper dbHelper;
    private UserSession userSession;

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
        setContentView(R.layout.activity_ordonnance);

        // Initialize database and session
        dbHelper = DatabaseHelper.getInstance(this);
        userSession = new UserSession(this);

        // Check if user is logged in
        if (userSession.getUserEmail() == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        prescriptionsContainer = findViewById(R.id.prescriptionsContainer);
        searchInput = findViewById(R.id.searchInput);

        // Initialize and set up bottom navigation
        initializeNavigation();

        // Set up search functionality
        setupSearch();

        // Set up add button
        FloatingActionButton fab = findViewById(R.id.addPrescriptionFab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(OrdonnancesActivity.this, AddPrescriptionActivity.class);
            startActivity(intent);
        });

        // Load prescriptions
        loadPrescriptions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrescriptions();
    }

    private void initializeNavigation() {
        // Find views
        homeIcon = findViewById(R.id.homeIcon);
        medicationIcon = findViewById(R.id.medicationIcon);
        renderVousIcon = findViewById(R.id.renderVousIcon);
        ordannanceIcon = findViewById(R.id.ordannanceIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // Set this icon as active
        ordannanceIcon.setColorFilter(getResources().getColor(R.color.primary));

        // Set click listeners
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(OrdonnancesActivity.this, MainActivity.class));
            finish();
        });

        medicationIcon.setOnClickListener(v -> {
            startActivity(new Intent(OrdonnancesActivity.this, MedicationsActivity.class));
            finish();
        });

        renderVousIcon.setOnClickListener(v -> {
            startActivity(new Intent(OrdonnancesActivity.this, RendezVousActivity.class));
            finish();
        });

        // Ordonnances is already active

        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(OrdonnancesActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadPrescriptions();
                } else {
                    searchPrescriptions(query);
                }
            }
        });
    }

    private void loadPrescriptions() {
        prescriptionsContainer.removeAllViews();

        List<Prescription> prescriptions = dbHelper.getPrescriptionsForUser(userSession.getUserEmail());

        if (prescriptions.isEmpty()) {
            showEmptyState();
        } else {
            for (Prescription prescription : prescriptions) {
                addPrescriptionCard(prescription);
            }
        }
    }

    private void searchPrescriptions(String query) {
        prescriptionsContainer.removeAllViews();

        List<Prescription> searchResults = dbHelper.searchPrescriptions(query, userSession.getUserEmail());

        if (searchResults.isEmpty()) {
            TextView noResults = new TextView(this);
            noResults.setText("Aucun résultat trouvé pour \"" + query + "\"");
            noResults.setTextSize(16);
            noResults.setPadding(0, 32, 0, 32);
            prescriptionsContainer.addView(noResults);
        } else {
            for (Prescription prescription : searchResults) {
                addPrescriptionCard(prescription);
            }
        }
    }

    private void showEmptyState() {
        TextView emptyText = new TextView(this);
        emptyText.setText("Aucune ordonnance trouvée. Ajoutez votre première ordonnance !");
        emptyText.setTextSize(16);
        emptyText.setPadding(0, 32, 0, 32);
        prescriptionsContainer.addView(emptyText);
    }

    private void addPrescriptionCard(final Prescription prescription) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.prescription_item, null);

        ImageView prescriptionImage = cardView.findViewById(R.id.prescriptionImage);
        TextView titleText = cardView.findViewById(R.id.prescriptionTitle);
        TextView descText = cardView.findViewById(R.id.prescriptionDesc);
        ImageView optionsIcon = cardView.findViewById(R.id.optionsIcon);

        // Set content
        titleText.setText(prescription.getTitle());
        descText.setText(prescription.getDescription());

        // Set appropriate image
        if (prescription.isPdf()) {
            prescriptionImage.setImageResource(R.drawable.pdf_placeholder);
        } else if (prescription.isImage()) {
            File imageFile = new File(prescription.getFilePath());
            if (imageFile.exists()) {
                prescriptionImage.setImageURI(Uri.fromFile(imageFile));
            } else {
                prescriptionImage.setImageResource(R.drawable.ordonnance_img);
            }
        } else {
            prescriptionImage.setImageResource(R.drawable.ordonnance_img);
        }

        // Add options menu
        optionsIcon.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(OrdonnancesActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.prescription_options, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_view) {
                    openPrescriptionFile(prescription);
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    deletePrescription(prescription);
                    return true;
                } else if (itemId == R.id.menu_share) {
                    sharePrescription(prescription);
                    return true;
                }
                return false;
            });

            popup.show();
        });

        // Set card click action (view file)
        cardView.setOnClickListener(v -> openPrescriptionFile(prescription));

        // Add some margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginDp = 8;
        int marginPx = (int) (marginDp * getResources().getDisplayMetrics().density);
        params.setMargins(0, marginPx, 0, marginPx);
        cardView.setLayoutParams(params);

        prescriptionsContainer.addView(cardView);
    }

    private void openPrescriptionFile(Prescription prescription) {
        File file = new File(prescription.getFilePath());
        if (!file.exists()) {
            Toast.makeText(this, "Fichier introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (prescription.isPdf()) {
                intent.setDataAndType(fileUri, "application/pdf");
            } else if (prescription.isImage()) {
                intent.setDataAndType(fileUri, "image/*");
            } else {
                intent.setDataAndType(fileUri, "*/*");
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Aucune application pour ouvrir ce fichier", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening file", e);
            Toast.makeText(this, "Erreur lors de l'ouverture du fichier", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePrescription(Prescription prescription) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer l'ordonnance")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette ordonnance ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    // Delete the file from storage
                    FileHelper.deleteFile(prescription.getFilePath());

                    // Delete from database
                    if (dbHelper.deletePrescription(prescription.getId())) {
                        Toast.makeText(OrdonnancesActivity.this, "Ordonnance supprimée", Toast.LENGTH_SHORT).show();
                        loadPrescriptions(); // Refresh list
                    } else {
                        Toast.makeText(OrdonnancesActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void sharePrescription(Prescription prescription) {
        File file = new File(prescription.getFilePath());
        if (!file.exists()) {
            Toast.makeText(this, "Fichier introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if (prescription.isPdf()) {
                shareIntent.setType("application/pdf");
            } else if (prescription.isImage()) {
                shareIntent.setType("image/*");
            } else {
                shareIntent.setType("*/*");
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Partager l'ordonnance"));
        } catch (Exception e) {
            Log.e(TAG, "Error sharing file", e);
            Toast.makeText(this, "Erreur lors du partage du fichier", Toast.LENGTH_SHORT).show();
        }
    }
}