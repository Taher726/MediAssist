package com.example.mediassist.ui.medication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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

    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent nav/status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(true);
        }

        setContentView(R.layout.activity_medications);
        initializeNavigation();

        // Open Add Medication Page
        findViewById(R.id.addMedicationButton).setOnClickListener(v ->
                startActivity(new Intent(MedicationsActivity.this, AddMedicationActivity.class))
        );
    }

    private void initializeNavigation() {
        homeIcon = findViewById(R.id.homeIcon);
        medicationIcon = findViewById(R.id.medicationIcon);
        renderVousIcon = findViewById(R.id.renderVousIcon);
        ordannanceIcon = findViewById(R.id.ordannanceIcon);
        profileIcon = findViewById(R.id.profileIcon);

        medicationIcon.setColorFilter(getResources().getColor(R.color.primary));

        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(MedicationsActivity.this, MainActivity.class));
            finish();
        });

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
