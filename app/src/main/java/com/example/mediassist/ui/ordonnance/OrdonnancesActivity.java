package com.example.mediassist.ui.ordonnance;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrdonnancesActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

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

        // Initialize and set up bottom navigation
        initializeNavigation();

        FloatingActionButton fab = findViewById(R.id.addPrescriptionFab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(OrdonnancesActivity.this, AddPrescriptionActivity.class);
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
}
