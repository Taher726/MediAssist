package com.example.mediassist.ui.rendezvous;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.data.models.Appointment;
import com.example.mediassist.ui.medication.MedicationsActivity;
import com.example.mediassist.ui.ordonnance.OrdonnancesActivity;
import com.example.mediassist.ui.profile.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RendezVousActivity extends AppCompatActivity {
    private static final String TAG = "RendezVousActivity";

    private LinearLayout appointmentsContainer;
    private FloatingActionButton addAppointmentButton;
    private DatabaseHelper dbHelper;
    private UserSession userSession;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window appearance
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.activity_rendez_vous);

        // Initialize session and get user
        userSession = new UserSession(this);
        userEmail = userSession.getUserEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in. Please login first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupNavigation();
        loadAppointments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments(); // Reload appointments when returning to this screen
    }

    private void initializeViews() {
        appointmentsContainer = findViewById(R.id.appointmentsContainer);
        addAppointmentButton = findViewById(R.id.addAppointmentButton);
        dbHelper = DatabaseHelper.getInstance(this);

        // Navigation icons
        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView medicationIcon = findViewById(R.id.medicationIcon);
        ImageView renderVousIcon = findViewById(R.id.renderVousIcon);
        ImageView ordannanceIcon = findViewById(R.id.ordannanceIcon);
        ImageView profileIcon = findViewById(R.id.profileIcon);

        // Highlight current tab
        renderVousIcon.setColorFilter(getResources().getColor(R.color.primary));
    }

    private void setupNavigation() {
        findViewById(R.id.homeIcon).setOnClickListener(v -> navigateTo(MainActivity.class));
        findViewById(R.id.medicationIcon).setOnClickListener(v -> navigateTo(MedicationsActivity.class));
        findViewById(R.id.ordannanceIcon).setOnClickListener(v -> navigateTo(OrdonnancesActivity.class));
        findViewById(R.id.profileIcon).setOnClickListener(v -> navigateTo(ProfileActivity.class));

        addAppointmentButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddRendezVousActivity.class))
        );
    }

    private void navigateTo(Class<?> cls) {
        startActivity(new Intent(this, cls));
        finish();
    }

    private void loadAppointments() {
        appointmentsContainer.removeAllViews();

        List<Appointment> appointments = dbHelper.getAppointmentsForUser(userEmail);

        // Add section title for upcoming appointments
        TextView upcomingTitle = new TextView(this);
        upcomingTitle.setText("Prochains rendez-vous");
        upcomingTitle.setTextSize(18);
        upcomingTitle.setTextColor(Color.BLACK);
        upcomingTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
        upcomingTitle.setPadding(0, 0, 0, 32);
        appointmentsContainer.addView(upcomingTitle);

        boolean hasUpcoming = false;
        boolean hasPast = false;

        // First add upcoming appointments
        for (Appointment appointment : appointments) {
            if (appointment.isUpcoming()) {
                addAppointmentCard(appointment);
                hasUpcoming = true;
            }
        }

        if (!hasUpcoming) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Aucun rendez-vous prévu");
            emptyView.setTextSize(16);
            emptyView.setPadding(0, 8, 0, 32);
            appointmentsContainer.addView(emptyView);
        }

        // Add section title for past appointments
        TextView pastTitle = new TextView(this);
        pastTitle.setText("Rendez-vous précédents");
        pastTitle.setTextSize(18);
        pastTitle.setTextColor(Color.BLACK);
        pastTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
        pastTitle.setPadding(0, 32, 0, 16);
        appointmentsContainer.addView(pastTitle);

        // Then add past appointments
        for (Appointment appointment : appointments) {
            if (!appointment.isUpcoming()) {
                addAppointmentCard(appointment);
                hasPast = true;
            }
        }

        if (!hasPast) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Aucun rendez-vous passé");
            emptyView.setTextSize(16);
            emptyView.setPadding(0, 8, 0, 32);
            appointmentsContainer.addView(emptyView);
        }
    }

    private void addAppointmentCard(final Appointment appointment) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.appointment_item, null);

        // Set card background color based on upcoming or past
        if (!appointment.isUpcoming()) {
            cardView.setCardBackgroundColor(Color.parseColor("#F0F0F0")); // Gray background for past appointments
        }

        TextView titleText = cardView.findViewById(R.id.appointmentTitle);
        TextView dateText = cardView.findViewById(R.id.appointmentDate);
        TextView placeText = cardView.findViewById(R.id.appointmentPlace);
        ImageView deleteIcon = cardView.findViewById(R.id.deleteIcon);

        titleText.setText(appointment.getTitle());
        dateText.setText(appointment.getFormattedDate());
        placeText.setText(appointment.getPlace());

        // Set delete icon click listener
        deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(RendezVousActivity.this)
                    .setTitle("Supprimer le rendez-vous")
                    .setMessage("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        if (dbHelper.deleteAppointment(appointment.getId())) {
                            Toast.makeText(RendezVousActivity.this, "Rendez-vous supprimé", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        } else {
                            Toast.makeText(RendezVousActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        // Set card click listener for editing
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(RendezVousActivity.this, AddRendezVousActivity.class);
            intent.putExtra("appointment_id", appointment.getId());
            intent.putExtra("appointment_title", appointment.getTitle());
            intent.putExtra("appointment_description", appointment.getDescription());
            intent.putExtra("appointment_date", appointment.getDate());
            intent.putExtra("appointment_place", appointment.getPlace());
            startActivity(intent);
        });

        // Add some margin between cards
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16); // 16dp bottom margin
        cardView.setLayoutParams(params);

        appointmentsContainer.addView(cardView);
    }
}