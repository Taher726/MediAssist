package com.example.mediassist.ui.profile;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;

public class ProfileActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    private ImageView profileImageView;
    private TextView profileNameTextView;
    private CardView cardSettings, cardInfo, cardNotifications;

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
        setContentView(R.layout.activity_profile);

        // Initialiser les vues
        profileImageView = findViewById(R.id.imageView2);
        profileNameTextView = findViewById(R.id.textView);
        cardSettings = findViewById(R.id.cardSettings);
        cardInfo = findViewById(R.id.cardInfo);
        cardNotifications = findViewById(R.id.cardNotifications);

        // Définir les actions pour les cartes
        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Naviguer vers la page des paramètres
                //Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                //startActivity(intent);
            }
        });

        cardInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Naviguer vers la page des informations personnelles
                /*Intent intent = new Intent(ProfileActivity.this, PersonalInfoActivity.class);
                startActivity(intent);*/
            }
        });

        cardNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Naviguer vers la page des notifications
                /*Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
                startActivity(intent);*/
            }
        });

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