package com.example.mediassist.ui.rendezvous;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.data.models.Appointment;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddRendezVousActivity extends AppCompatActivity {
    private static final String TAG = "AddAppointmentActivity";

    private ImageView backIcon;
    private TextInputEditText titleInput, descriptionInput, dateInput, placeInput;
    private Button addButton;

    private DatabaseHelper dbHelper;
    private UserSession userSession;

    private int appointmentId = -1;
    private boolean isEditMode = false;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rendezvous);

        initializeViews();
        setupListeners();

        // Check if we're in edit mode
        if (getIntent().hasExtra("appointment_id")) {
            isEditMode = true;
            appointmentId = getIntent().getIntExtra("appointment_id", -1);
            populateFormForEdit();
        }
    }

    private void initializeViews() {
        backIcon = findViewById(R.id.backIcon);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateInput = findViewById(R.id.dateInput);
        placeInput = findViewById(R.id.placeInput);
        addButton = findViewById(R.id.addButton);

        dbHelper = DatabaseHelper.getInstance(this);
        userSession = new UserSession(this);
        selectedDateTime = Calendar.getInstance();

        if (isEditMode) {
            addButton.setText("Modifier");
        }
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        dateInput.setOnClickListener(v -> showDateTimePicker());

        addButton.setOnClickListener(v -> saveAppointment());
    }

    private void showDateTimePicker() {
        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // After date is selected, show time picker
                    new TimePickerDialog(
                            AddRendezVousActivity.this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);

                                // Update text field
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                dateInput.setText(sdf.format(selectedDateTime.getTime()));
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void populateFormForEdit() {
        // Populate fields with existing data
        titleInput.setText(getIntent().getStringExtra("appointment_title"));
        descriptionInput.setText(getIntent().getStringExtra("appointment_description"));
        dateInput.setText(getIntent().getStringExtra("appointment_date"));
        placeInput.setText(getIntent().getStringExtra("appointment_place"));

        // Parse the date string to set selectedDateTime
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            selectedDateTime.setTime(sdf.parse(getIntent().getStringExtra("appointment_date")));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date", e);
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(titleInput.getText())) {
            titleInput.setError("Le titre est requis");
            return false;
        }

        if (TextUtils.isEmpty(dateInput.getText())) {
            dateInput.setError("La date est requise");
            return false;
        }

        if (TextUtils.isEmpty(placeInput.getText())) {
            placeInput.setError("Le lieu est requis");
            return false;
        }

        return true;
    }

    private void saveAppointment() {
        if (!validateInputs()) {
            return;
        }

        String userEmail = userSession.getUserEmail();
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setTitle(titleInput.getText().toString().trim());
        appointment.setDescription(descriptionInput.getText().toString().trim());
        appointment.setDate(dateInput.getText().toString().trim());
        appointment.setPlace(placeInput.getText().toString().trim());
        appointment.setStatus("Upcoming"); // Default status

        if (isEditMode && appointmentId != -1) {
            // Update existing appointment
            appointment.setId(appointmentId);
            boolean updated = dbHelper.updateAppointment(appointment, userEmail);

            if (updated) {
                Toast.makeText(this, "Rendez-vous mis à jour", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add new appointment
            long id = dbHelper.addAppointment(appointment, userEmail);

            if (id != -1) {
                Toast.makeText(this, "Rendez-vous ajouté", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        }
    }
}