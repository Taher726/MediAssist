package com.example.mediassist.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.data.database.UserSession;

public class ProfileActivity extends AppCompatActivity {

    // UI elements
    private TextView userName;
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;
    private CardView cardSettings, cardInfo, cardNotifications, cardLogout;

    // User session for managing logged-in state
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
        setContentView(R.layout.activity_profile);

        // Initialize user session
        userSession = new UserSession(this);

        // Initialize views
        initializeViews();

        // Set dynamic user information
        loadUserProfile();

        // Set bottom navigation listeners
        setNavigationListeners();

        // Set card action listeners
        setCardListeners();
    }

    private void initializeViews() {
        // User profile elements
        userName = findViewById(R.id.textView);

        // Profile action cards
        cardSettings = findViewById(R.id.cardSettings);
        cardInfo = findViewById(R.id.cardInfo);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardLogout = findViewById(R.id.cardLogout);

        // Bottom navigation icons
        homeIcon = findViewById(R.id.homeIcon);
        medicationIcon = findViewById(R.id.medicationIcon);
        renderVousIcon = findViewById(R.id.renderVousIcon);
        ordannanceIcon = findViewById(R.id.ordannanceIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // Set profile as active in the bottom nav
        resetNavigationColors();
        profileIcon.setColorFilter(getResources().getColor(R.color.primary));
    }

    private void loadUserProfile() {
        // Get user information from session
        String name = userSession.getUserName();

        // Set user name (fall back to email if name is not available)
        if (name != null && !name.isEmpty()) {
            userName.setText(name);
        } else {
            String email = userSession.getUserEmail();
            if (email != null) {
                // Just using email as name if no name is available
                userName.setText(email.split("@")[0]);
            } else {
                userName.setText("User");
            }
        }
    }

    private void setNavigationListeners() {
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });

        medicationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.mediassist.ui.medication.MedicationsActivity.class);
            startActivity(intent);
            finish();
        });

        renderVousIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.mediassist.ui.rendezvous.RendezVousActivity.class);
            startActivity(intent);
            finish();
        });

        ordannanceIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, com.example.mediassist.ui.ordonnance.OrdonnancesActivity.class);
            startActivity(intent);
            finish();
        });

        // Profile icon - we're already on this page
        profileIcon.setOnClickListener(v -> {
            // Already on profile page, do nothing or refresh
            resetNavigationColors();
            profileIcon.setColorFilter(getResources().getColor(R.color.primary));
        });
    }

    private void setCardListeners() {
        // Settings card
        cardSettings.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Paramètres du compte", Toast.LENGTH_SHORT).show();
            // TODO: Implement settings activity
            // Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            // startActivity(intent);
        });

        // Personal info card
        cardInfo.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Informations personnelles", Toast.LENGTH_SHORT).show();
            // TODO: Implement personal info activity
            // Intent intent = new Intent(ProfileActivity.this, PersonalInfoActivity.class);
            // startActivity(intent);
        });

        // Notifications card
        cardNotifications.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Implement notifications activity
            // Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            // startActivity(intent);
        });

        // Logout card
        cardLogout.setOnClickListener(v -> {
            // Show confirmation toast
            Toast.makeText(ProfileActivity.this, "Déconnexion en cours...", Toast.LENGTH_SHORT).show();

            // Perform logout
            userSession.logout();

            // Activity will be finished by the logout method in UserSession
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
}