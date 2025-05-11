package com.example.mediassist.ui.medication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.data.models.Medication;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.util.Log;

public class AddMedicationActivity extends AppCompatActivity {
    // And make sure you have this line in your class
    private static final String TAG = "AddMedicationActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imagePicker;
    private TextInputEditText nameInput, typeInput, frequencyInput, dosageInput, notesInput;
    private Button addTimeButton, addButton;
    private LinearLayout timesContainer, daysContainer;
    private ImageView backIcon;

    private DatabaseHelper dbHelper;
    private UserSession userSession;
    private Uri selectedImageUri = null;
    private byte[] imageData = null;

    private List<String> selectedTimes = new ArrayList<>();
    private List<String> selectedDays = new ArrayList<>();
    private boolean isEditMode = false;
    private int medicationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        initializeViews();
        setupListeners();
        initializeDaysUI();

        // Check if we're in edit mode
        if (getIntent().hasExtra("medication_id")) {
            isEditMode = true;
            medicationId = getIntent().getIntExtra("medication_id", -1);
            populateFormForEdit();
        }
    }

    private void initializeViews() {
        imagePicker = findViewById(R.id.imagePicker);
        nameInput = findViewById(R.id.nameInput);
        typeInput = findViewById(R.id.typeInput);
        frequencyInput = findViewById(R.id.frequencyInput);
        dosageInput = findViewById(R.id.dosageInput);
        notesInput = findViewById(R.id.notesInput);
        addTimeButton = findViewById(R.id.addTimeButton);
        addButton = findViewById(R.id.addButton);
        timesContainer = findViewById(R.id.timesContainer);
        daysContainer = findViewById(R.id.daysContainer);
        backIcon = findViewById(R.id.backIcon);

        dbHelper = DatabaseHelper.getInstance(this);
        userSession = new UserSession(this);

        if (isEditMode) {
            addButton.setText("Update Medication");
        }
    }

    private void setupListeners() {
        backIcon.setOnClickListener(v -> finish());

        imagePicker.setOnClickListener(v -> openImagePicker());

        addTimeButton.setOnClickListener(v -> showTimePickerDialog());

        addButton.setOnClickListener(v -> saveMedication());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                // Resize bitmap to reduce memory usage and database size
                bitmap = getResizedBitmap(bitmap, 500); // 500px max width/height

                imagePicker.setImageBitmap(bitmap);

                // Convert bitmap to byte array with compression
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                imageData = stream.toByteArray();

                Log.d(TAG, "Image selected and processed, size: " + imageData.length + " bytes");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper method to resize the bitmap
    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void showTimePickerDialog() {
        View timePickerView = getLayoutInflater().inflate(R.layout.time_picker_dialog, null);
        TimePicker timePicker = timePickerView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Time")
                .setView(timePickerView)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();
                    String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

                    if (!selectedTimes.contains(timeStr)) {
                        selectedTimes.add(timeStr);
                        updateTimesUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void updateTimesUI() {
        timesContainer.removeAllViews();

        for (String time : selectedTimes) {
            View timeView = getLayoutInflater().inflate(R.layout.time_item, null);
            TextView timeText = timeView.findViewById(R.id.timeText);
            ImageView deleteIcon = timeView.findViewById(R.id.deleteTimeIcon);

            timeText.setText(time);
            deleteIcon.setOnClickListener(v -> {
                selectedTimes.remove(time);
                updateTimesUI();
            });

            timesContainer.addView(timeView);
        }
    }

    private void initializeDaysUI() {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (String day : daysOfWeek) {
            View dayView = getLayoutInflater().inflate(R.layout.day_checkbox_item, null);
            CheckBox dayCheckbox = dayView.findViewById(R.id.dayCheckbox);

            dayCheckbox.setText(day);
            dayCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedDays.contains(day)) {
                        selectedDays.add(day);
                    }
                } else {
                    selectedDays.remove(day);
                }
            });

            daysContainer.addView(dayView);
        }
    }

    private void populateFormForEdit() {
        // Populate the form with existing medication data
        nameInput.setText(getIntent().getStringExtra("medication_name"));
        typeInput.setText(getIntent().getStringExtra("medication_type"));
        frequencyInput.setText(getIntent().getStringExtra("medication_frequency"));
        dosageInput.setText(getIntent().getStringExtra("medication_dosage"));
        notesInput.setText(getIntent().getStringExtra("medication_notes"));

        // Set selected times
        String times = getIntent().getStringExtra("medication_times");
        if (times != null && !times.isEmpty()) {
            selectedTimes = Arrays.asList(times.split(","));
            updateTimesUI();
        }

        // Set selected days
        String days = getIntent().getStringExtra("medication_days");
        if (days != null && !days.isEmpty()) {
            selectedDays = new ArrayList<>(Arrays.asList(days.split(",")));

            // Check the corresponding checkboxes
            for (int i = 0; i < daysContainer.getChildCount(); i++) {
                View dayView = daysContainer.getChildAt(i);
                CheckBox dayCheckbox = dayView.findViewById(R.id.dayCheckbox);
                String day = dayCheckbox.getText().toString();

                if (selectedDays.contains(day)) {
                    dayCheckbox.setChecked(true);
                }
            }
        }

        // Load the image if available
        String imagePath = getIntent().getStringExtra("medication_image_path");
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imagePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePicker.setImageBitmap(bitmap);
                selectedImageUri = imageUri;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        if (nameInput.getText().toString().trim().isEmpty()) {
            nameInput.setError("Medication name is required");
            return false;
        }

        if (typeInput.getText().toString().trim().isEmpty()) {
            typeInput.setError("Type is required");
            return false;
        }

        if (dosageInput.getText().toString().trim().isEmpty()) {
            dosageInput.setError("Dosage is required");
            return false;
        }

        if (selectedTimes.isEmpty()) {
            Toast.makeText(this, "Please add at least one time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveMedication() {
        if (!validateInputs()) {
            return;
        }

        Medication medication = new Medication();
        medication.setName(nameInput.getText().toString().trim());
        medication.setType(typeInput.getText().toString().trim());
        medication.setFrequency(frequencyInput.getText().toString().trim());
        medication.setDosage(dosageInput.getText().toString().trim());
        medication.setNotes(notesInput.getText().toString().trim());

        // Join times with comma
        medication.setTime(String.join(",", selectedTimes));

        // Join days with comma
        medication.setDays(String.join(",", selectedDays));

        // Set default status
        medication.setStatus("Active");

        // Set image data
        if (imageData != null) {
            medication.setImageData(imageData);
            Log.d(TAG, "Setting image data size: " + imageData.length + " bytes");
        }

        if (selectedImageUri != null) {
            medication.setImagePath(selectedImageUri.toString());
            Log.d(TAG, "Setting image path: " + selectedImageUri);
        }

        String userEmail = userSession.getUserEmail();

        Log.d(TAG, "Adding medication for user: " + userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        long result;

        if (isEditMode && medicationId != -1) {
            // Update existing medication
            medication.setId(medicationId);
            result = dbHelper.updateMedication(medication, userEmail);

            if (result > 0) {
                Log.d(TAG, "Medication updated successfully");
                Toast.makeText(this, "Medication updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e(TAG, "Failed to update medication");
                Toast.makeText(this, "Failed to update medication", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add new medication
            result = dbHelper.addMedication(medication, userEmail);

            if (result != -1) {
                Log.d(TAG, "Medication added successfully with ID: " + result);
                Toast.makeText(this, "Medication added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e(TAG, "Failed to add medication");
                Toast.makeText(this, "Failed to add medication", Toast.LENGTH_SHORT).show();
            }
        }
    }
}