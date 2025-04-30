package com.example.mediassist;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediassist.ui.horaire.HoraireActivity;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;

public class MainActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    // Card Views
    private ConstraintLayout medicationsCard, renderVousCard, ordannanceCard, horaireCard;

    // Statistics
    private TextView totalMedi, totalRendez, totalOrdo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeViews();

        // Set click listeners
        setNavigationListeners();
        setCardListeners();

        // Set mock data for statistics
        loadStatistics();
    }

    private void initializeViews() {
        // Bottom Navigation
        homeIcon = findViewById(R.id.homeIcon);
        medicationIcon = findViewById(R.id.medicationIcon);
        renderVousIcon = findViewById(R.id.renderVousIcon);
        ordannanceIcon = findViewById(R.id.ordannanceIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // Cards
        medicationsCard = findViewById(R.id.medicationsCard);
        renderVousCard = findViewById(R.id.renderVousCard);
        ordannanceCard = findViewById(R.id.ordannanceCard);
        horaireCard = findViewById(R.id.horaireCard);

        // Statistics
        totalMedi = findViewById(R.id.totalMedi);
        totalRendez = findViewById(R.id.totalRendez);
        totalOrdo = findViewById(R.id.totalOrdo);
    }

    private void setNavigationListeners() {
        // Set Home as active by default (it's already active since we're in MainActivity)
        homeIcon.setColorFilter(getResources().getColor(R.color.primary));

        // Set click listeners for bottom navigation
        homeIcon.setOnClickListener(v -> {
            // Already on home page, do nothing or refresh
            resetNavigationColors();
            homeIcon.setColorFilter(getResources().getColor(R.color.primary));
        });

        medicationIcon.setOnClickListener(v -> {
            resetNavigationColors();
            medicationIcon.setColorFilter(getResources().getColor(R.color.primary));
            startActivity(new Intent(MainActivity.this, MedicationsActivity.class));
        });

        renderVousIcon.setOnClickListener(v -> {
            resetNavigationColors();
            renderVousIcon.setColorFilter(getResources().getColor(R.color.primary));
            startActivity(new Intent(MainActivity.this, RendezVousActivity.class));
        });

        ordannanceIcon.setOnClickListener(v -> {
            resetNavigationColors();
            ordannanceIcon.setColorFilter(getResources().getColor(R.color.primary));
            startActivity(new Intent(MainActivity.this, OrdonnancesActivity.class));
        });

        profileIcon.setOnClickListener(v -> {
            resetNavigationColors();
            profileIcon.setColorFilter(getResources().getColor(R.color.primary));
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    private void resetNavigationColors() {
        // Reset all icons to default color
        homeIcon.setColorFilter(getResources().getColor(android.R.color.black));
        medicationIcon.setColorFilter(getResources().getColor(android.R.color.black));
        renderVousIcon.setColorFilter(getResources().getColor(android.R.color.black));
        ordannanceIcon.setColorFilter(getResources().getColor(android.R.color.black));
        profileIcon.setColorFilter(getResources().getColor(android.R.color.black));
    }

    private void setCardListeners() {
        // Set click listeners for each card
        medicationsCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MedicationsActivity.class));
        });

        renderVousCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RendezVousActivity.class));
        });

        ordannanceCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OrdonnancesActivity.class));
        });

        horaireCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HoraireActivity.class));
        });
    }

    private void loadStatistics() {
        // Mock data - in a real app, these would come from a database
        totalMedi.setText("5");
        totalRendez.setText("3");
        totalOrdo.setText("7");
    }
}