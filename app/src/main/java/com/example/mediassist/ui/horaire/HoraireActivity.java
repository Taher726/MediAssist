package com.example.mediassist.ui.horaire;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;

public class HoraireActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horaire);

        // Initialize and set up bottom navigation
        initializeNavigation();
    }

    private void initializeNavigation() {
        // Find views
        homeIcon = findViewById(R.id.homeIcon);
        medicationIcon = findViewById(R.id.medicationIcon);
        renderVousIcon = findViewById(R.id.renderVousIcon);
        ordannanceIcon = findViewById(R.id.ordannanceIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // No specific icon active as Horaire doesn't have a direct nav icon

        // Set click listeners
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(HoraireActivity.this, MainActivity.class));
            finish();
        });

        medicationIcon.setOnClickListener(v -> {
            startActivity(new Intent(HoraireActivity.this, MedicationsActivity.class));
            finish();
        });

        renderVousIcon.setOnClickListener(v -> {
            startActivity(new Intent(HoraireActivity.this, RendezVousActivity.class));
            finish();
        });

        ordannanceIcon.setOnClickListener(v -> {
            startActivity(new Intent(HoraireActivity.this, OrdonnancesActivity.class));
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(HoraireActivity.this, ProfileActivity.class));
            finish();
        });
    }
}