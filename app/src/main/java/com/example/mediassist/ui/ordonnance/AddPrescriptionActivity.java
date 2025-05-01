package com.example.mediassist.ui.ordonnance;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddPrescriptionActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 101;

    private ImageView filePicker;
    private TextInputEditText titleInput, descInput;
    private Uri selectedFileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ordonnance);

        // UI Immersive
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }

        // Initialisation
        filePicker = findViewById(R.id.filePicker);
        titleInput = findViewById(R.id.titleInput);
        descInput = findViewById(R.id.descInput);
        MaterialButton addButton = findViewById(R.id.addPrescriptionBtn);
        ImageView backIcon = findViewById(R.id.backIcon);

        // Retour
        backIcon.setOnClickListener(v -> finish());

        // Sélection fichier
        filePicker.setOnClickListener(v -> pickFile());

        // Ajouter l’ordonnance
        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();

            if (title.isEmpty() || selectedFileUri == null) {
                Toast.makeText(this, "Veuillez remplir le titre et choisir un fichier.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Enregistrer dans Firebase ou SQLite
            Toast.makeText(this, "Ordonnance ajoutée !", Toast.LENGTH_SHORT).show();
            finish();
        });
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
                String mimeType = getContentResolver().getType(selectedFileUri);
                if (mimeType != null && mimeType.contains("image")) {
                    filePicker.setImageURI(selectedFileUri); // Affiche image
                } else {
                    filePicker.setImageResource(R.drawable.pdf_placeholder); // Icône PDF
                }

                // Tu peux aussi lire le nom du fichier ici si besoin :
                String fileName = getFileName(selectedFileUri);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                result = cursor.getString(nameIndex);
            }
        }
        return result;
    }
}
