package com.example.mediassist.ui.ordonnance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.database.UserSession;
import com.example.mediassist.data.models.Prescription;
import com.example.mediassist.utils.FileHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddPrescriptionActivity extends AppCompatActivity {
    private static final String TAG = "AddPrescriptionActivity";
    private static final int PICK_FILE_REQUEST = 101;

    private ImageView filePicker;
    private TextInputEditText titleInput, descInput;
    private MaterialButton addButton;
    private Uri selectedFileUri = null;

    private DatabaseHelper dbHelper;
    private UserSession userSession;
    private String fileType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ordonnance);

        // UI Immersive
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }

        // Initialize helpers
        dbHelper = DatabaseHelper.getInstance(this);
        userSession = new UserSession(this);

        // Check if user is logged in
        if (userSession.getUserEmail() == null) {
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialisation
        filePicker = findViewById(R.id.filePicker);
        titleInput = findViewById(R.id.titleInput);
        descInput = findViewById(R.id.descInput);
        addButton = findViewById(R.id.addPrescriptionBtn);
        ImageView backIcon = findViewById(R.id.backIcon);

        // Retour
        backIcon.setOnClickListener(v -> finish());

        // Sélection fichier
        filePicker.setOnClickListener(v -> pickFile());

        // Ajouter l'ordonnance
        addButton.setOnClickListener(v -> savePrescription());
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                // Determine file type
                fileType = FileHelper.getFileType(this, selectedFileUri);

                // Update UI based on file type
                if ("image".equals(fileType)) {
                    try {
                        // For images, show a thumbnail
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(selectedFileUri));
                        filePicker.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image", e);
                        filePicker.setImageResource(R.drawable.ordonnance_img);
                    }
                } else if ("pdf".equals(fileType)) {
                    // For PDFs, show icon
                    filePicker.setImageResource(R.drawable.pdf_placeholder);
                } else {
                    // For other files, show a generic icon
                    filePicker.setImageResource(R.drawable.ordonnance_img);
                }

                // If title is empty, suggest file name as title
                if (TextUtils.isEmpty(titleInput.getText())) {
                    String fileName = FileHelper.getFileName(this, selectedFileUri);
                    if (fileName != null) {
                        // Remove extension
                        int dotPos = fileName.lastIndexOf(".");
                        if (dotPos > 0) {
                            fileName = fileName.substring(0, dotPos);
                        }
                        titleInput.setText(fileName);
                    }
                }
            }
        }
    }

    private void savePrescription() {
        String title = titleInput.getText().toString().trim();
        String description = descInput.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Le titre est obligatoire");
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Veuillez sélectionner un fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save file to app's private storage
        String filePath = FileHelper.saveFileToAppStorage(this, selectedFileUri);

        if (filePath == null) {
            Toast.makeText(this, "Erreur lors de l'enregistrement du fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create prescription object
        Prescription prescription = new Prescription();
        prescription.setTitle(title);
        prescription.setDescription(description);
        prescription.setFilePath(filePath);
        prescription.setFileType(fileType);
        prescription.setFileName(FileHelper.getFileName(this, selectedFileUri));
        prescription.setFileSize(FileHelper.getFileSize(this, selectedFileUri));
        prescription.setDateAdded(System.currentTimeMillis());

        // Add to database
        long id = dbHelper.addPrescription(prescription, userSession.getUserEmail());

        if (id != -1) {
            Toast.makeText(this, "Ordonnance ajoutée avec succès", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout de l'ordonnance", Toast.LENGTH_SHORT).show();
        }
    }
}