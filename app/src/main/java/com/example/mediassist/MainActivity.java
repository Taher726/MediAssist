package com.example.mediassist;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.ui.auth.LoginActivity;
import com.example.mediassist.ui.horaire.HoraireActivity;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.example.mediassist.ui.rendezvous.RendezVousActivity;
import com.example.mediassist.ui.urgence.UrgenceActivity;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Bottom Navigation Icons
    private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    // Card Views
    private ConstraintLayout urgenceCard, horaireCard;

    // Statistics
    private TextView totalMedi, totalRendez, totalOrdo;

    // User session manager
    private UserSession userSession;

    // Database helper
    private DatabaseHelper dbHelper;

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

        // Initialize user session and database
        userSession = new UserSession(this);
        dbHelper = DatabaseHelper.getInstance(this);

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

        // Load real statistics from database
        loadStatisticsFromDatabase();

        // Request notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check login status on resume
        if (!userSession.isLoggedIn()) {
            userSession.logout(); // This will redirect to login screen
            return;
        }

        // Refresh statistics each time the activity is resumed
        loadStatisticsFromDatabase();
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
            loadStatisticsFromDatabase(); // Refresh statistics
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

    /**
     * Load real statistics from database for the current user
     */
    private void loadStatisticsFromDatabase() {
        String userEmail = userSession.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "User email is null or empty");
            return;
        }

        try {
            // Get the counts from database
            int medicationCount = dbHelper.getMedicationCount(userEmail);
            int appointmentCount = dbHelper.getAppointmentCount(userEmail);
            int prescriptionCount = dbHelper.getPrescriptionCount(userEmail);

            // Update the UI
            totalMedi.setText(String.valueOf(medicationCount));
            totalRendez.setText(String.valueOf(appointmentCount));
            totalOrdo.setText(String.valueOf(prescriptionCount));

            Log.d(TAG, "Statistics updated: Medications=" + medicationCount +
                    ", Appointments=" + appointmentCount +
                    ", Prescriptions=" + prescriptionCount);
        } catch (Exception e) {
            Log.e(TAG, "Error loading statistics", e);
            // If there's an error, show zeros
            totalMedi.setText("0");
            totalRendez.setText("0");
            totalOrdo.setText("0");
        }
    }
}