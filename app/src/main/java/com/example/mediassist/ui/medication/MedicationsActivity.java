package com.example.mediassist.ui.medication;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;

public class MedicationsActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make navigation bar transparent
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // Make the content display behind navigation bar (immersive mode)
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);
        setContentView(R.layout.activity_medications);

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
        medicationIcon.setColorFilter(getResources().getColor(R.color.primary));

        // Set click listeners
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(MedicationsActivity.this, MainActivity.class));
            finish();
        });

        // Medication is already active

        renderVousIcon.setOnClickListener(v -> {
            startActivity(new Intent(MedicationsActivity.this, RendezVousActivity.class));
            finish();
        });

        ordannanceIcon.setOnClickListener(v -> {
            startActivity(new Intent(MedicationsActivity.this, OrdonnancesActivity.class));
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(MedicationsActivity.this, ProfileActivity.class));
            finish();
        });
    }
}