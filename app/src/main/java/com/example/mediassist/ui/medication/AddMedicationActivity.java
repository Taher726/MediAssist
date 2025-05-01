package com.example.mediassist.ui.medication;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.models.Medication;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMedicationActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePicker;
    private EditText nameInput, typeInput, frequencyInput, dosageInput, notesInput;
    private MaterialButton addButton;
    private Button addTimeButton;
    private LinearLayout timesContainer, daysContainer;
    private Bitmap selectedImageBitmap;
    private String selectedImagePath;
    private List<String> selectedTimes = new ArrayList<>();
    private List<String> selectedDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        initializeViews();
        setupDayCheckboxes();
        setupClickListeners();

        // Ensure default user exists
        //DatabaseHelper.getInstance(this).ensureDefaultUserExists();
    }

    private void initializeViews() {
        imagePicker = findViewById(R.id.imagePicker);
        nameInput = findViewById(R.id.nameInput);
        typeInput = findViewById(R.id.typeInput);
        frequencyInput = findViewById(R.id.frequencyInput);
        dosageInput = findViewById(R.id.dosageInput);
        notesInput = findViewById(R.id.notesInput);
        addButton = findViewById(R.id.addButton);
        addTimeButton = findViewById(R.id.addTimeButton);
        timesContainer = findViewById(R.id.timesContainer);
        daysContainer = findViewById(R.id.daysContainer);
    }

    private void setupDayCheckboxes() {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(day);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedDays.add(day);
                } else {
                    selectedDays.remove(day);
                }
            });
            daysContainer.addView(checkBox);
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.backIcon).setOnClickListener(v -> finish());
        imagePicker.setOnClickListener(v -> openImageChooser());
        addButton.setOnClickListener(v -> saveMedication());
        addTimeButton.setOnClickListener(v -> showTimePicker());
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute1);
                    if (!selectedTimes.contains(time)) {
                        selectedTimes.add(time);
                        addTimeView(time);
                    } else {
                        Toast.makeText(this, "This time is already added", Toast.LENGTH_SHORT).show();
                    }
                },
                hour,
                minute,
                true
        );
        timePickerDialog.setTitle("Select Medication Time");
        timePickerDialog.show();
    }

    private void addTimeView(String time) {
        View timeView = getLayoutInflater().inflate(R.layout.time_item, null);
        TextView timeText = timeView.findViewById(R.id.timeText);
        ImageView deleteIcon = timeView.findViewById(R.id.deleteTimeIcon);

        timeText.setText(time);
        deleteIcon.setOnClickListener(v -> {
            selectedTimes.remove(time);
            timesContainer.removeView(timeView);
        });

        timesContainer.addView(timeView);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Medication Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imagePicker.setImageBitmap(selectedImageBitmap);
                selectedImagePath = data.getData().toString();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                Log.e("AddMedication", "Image load error", e);
            }
        }
    }

    private void saveMedication() {
        String name = nameInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();
        String frequency = frequencyInput.getText().toString().trim();
        String dosage = dosageInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (name.isEmpty()) {
            showError("Please enter medication name");
            return;
        }
        if (selectedTimes.isEmpty()) {
            showError("Please add at least one time");
            return;
        }
        if (selectedDays.isEmpty()) {
            showError("Please select at least one day");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving medication...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                Medication medication = new Medication();
                medication.setName(name);
                medication.setType(type);
                medication.setFrequency(frequency);
                medication.setDosage(dosage);
                medication.setTime(String.join(",", selectedTimes));
                medication.setDays(String.join(",", selectedDays));
                medication.setNotes(notes);
                medication.setStatus("Active");

                if (selectedImageBitmap != null) {
                    medication.setImageData(DatabaseHelper.getBytesFromBitmap(selectedImageBitmap));
                }
                medication.setImagePath(selectedImagePath);

                DatabaseHelper dbHelper = DatabaseHelper.getInstance(AddMedicationActivity.this);
                long medicationId = dbHelper.addMedication(medication, "user@example.com");

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (medicationId != -1) {
                        Toast.makeText(AddMedicationActivity.this, "Medication added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError("Failed to add medication. Please try again.");
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    showError("Error: " + e.getMessage());
                    Log.e("AddMedication", "Save error", e);
                });
            }
        }).start();
    }

    private void showError(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}