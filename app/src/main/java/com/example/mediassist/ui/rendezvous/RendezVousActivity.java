package com.example.mediassist.ui.rendezvous;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RendezVousActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transparent nav/status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.activity_rendez_vous);

        // Initialize and set up bottom navigation
        initializeNavigation();
        FloatingActionButton addButton = findViewById(R.id.addAppointmentButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(RendezVousActivity.this, AddRendezVousActivity.class);
            startActivity(intent);
        });

    }

    private void initializeNavigation() {
        // Find views
        homeIcon = findViewById(R.id.homeIcon);
        medicationIcon = findViewById(R.id.medicationIcon);
        renderVousIcon = findViewById(R.id.renderVousIcon);
        ordannanceIcon = findViewById(R.id.ordannanceIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // Set this icon as active
        renderVousIcon.setColorFilter(getResources().getColor(R.color.primary));

        // Set click listeners
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(RendezVousActivity.this, MainActivity.class));
            finish();
        });

        medicationIcon.setOnClickListener(v -> {
            startActivity(new Intent(RendezVousActivity.this, MedicationsActivity.class));
            finish();
        });

        // Rendez-vous is already active

        ordannanceIcon.setOnClickListener(v -> {
            startActivity(new Intent(RendezVousActivity.this, OrdonnancesActivity.class));
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(RendezVousActivity.this, ProfileActivity.class));
            finish();
        });
    }
}