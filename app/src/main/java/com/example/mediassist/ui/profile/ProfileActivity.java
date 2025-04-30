package com.example.mediassist.ui.profile;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;

public class ProfileActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        // Set this icon as active
        profileIcon.setColorFilter(getResources().getColor(R.color.primary));

        // Set click listeners
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        medicationIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MedicationsActivity.class));
            finish();
        });

        renderVousIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, RendezVousActivity.class));
            finish();
        });

        ordannanceIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrdonnancesActivity.class));
            finish();
        });

        // Profile is already active
    }
}