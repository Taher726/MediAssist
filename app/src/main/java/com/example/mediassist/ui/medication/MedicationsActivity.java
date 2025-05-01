package com.example.mediassist.ui.medication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.mediassist.data.models.Medication;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MedicationsActivity extends AppCompatActivity {
    private LinearLayout medicationsContainer;
    private DatabaseHelper dbHelper;
    private FloatingActionButton addMedicationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medications);

        initializeViews();
        setupNavigation();
        loadMedications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedications();
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
        String userEmail = "user@example.com";
        List<Medication> medications = dbHelper.getMedicationsForUser(userEmail);

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
        emptyView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        medicationsContainer.addView(emptyView);
    }

    private void addMedicationCard(Medication medication) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.medication_item, null);

        TextView nameText = cardView.findViewById(R.id.medName);
        TextView typeText = cardView.findViewById(R.id.medType);
        TextView frequencyText = cardView.findViewById(R.id.medFrequency);
        TextView dosageText = cardView.findViewById(R.id.medDosage);
        TextView timesText = cardView.findViewById(R.id.medTimes);
        TextView daysText = cardView.findViewById(R.id.medDays);
        TextView notesText = cardView.findViewById(R.id.medNotes);
        TextView statusIndicator = cardView.findViewById(R.id.statusIndicator);
        ImageView medIcon = cardView.findViewById(R.id.medIcon);
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

        if (medication.getImageData() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(medication.getImageData(), 0, medication.getImageData().length);
            medIcon.setImageBitmap(bitmap);
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
}