package com.example.mediassist;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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

import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.ui.auth.LoginActivity;
import com.example.mediassist.ui.horaire.HoraireActivity;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;
import com.example.mediassist.ui.urgence.UrgenceActivity;

public class MainActivity extends AppCompatActivity {

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    // Card Views
    private ConstraintLayout urgenceCard, horaireCard;

    // Statistics
    private TextView totalMedi, totalRendez, totalOrdo;

    // User session manager
    private UserSession userSession;

    // User welcome message
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transparent nav/status bar
        // Transparent nav bar and colored status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize user session
        userSession = new UserSession(this);

        // Check if user is logged in, if not redirect to login
        if (!userSession.isLoggedIn()) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Initialize UI components
        initializeViews();

        // Set user name in welcome message
        String userName = userSession.getUserName();
        if (userName != null && !userName.isEmpty()) {
            welcomeText.setText("Bienvenue " + userName);
        }

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
        urgenceCard = findViewById(R.id.urgenceCard);
        horaireCard = findViewById(R.id.horaireCard);

        // Statistics
        totalMedi = findViewById(R.id.totalMedi);
        totalRendez = findViewById(R.id.totalRendez);
        totalOrdo = findViewById(R.id.totalOrdo);

        // Welcome text
        welcomeText = findViewById(R.id.textView2);
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
        urgenceCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UrgenceActivity.class));
        });
        horaireCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HoraireActivity.class));
        });
    }

    private void loadStatistics() {
        // In a real app, these would come from a database
        // For now, using mock data but this could be enhanced to load real user data
        String userEmail = userSession.getUserEmail();
        // TODO: Load actual statistics based on the logged-in user
        totalMedi.setText("5");
        totalRendez.setText("3");
        totalOrdo.setText("7");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status on resume
        if (!userSession.isLoggedIn()) {
            userSession.logout(); // This will redirect to login screen
        }
    }
}