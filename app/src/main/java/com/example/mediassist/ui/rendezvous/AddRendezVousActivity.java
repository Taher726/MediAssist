package com.example.mediassist.ui.rendezvous;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediassist.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddRendezVousActivity extends AppCompatActivity {

    private TextInputEditText titleInput, descriptionInput, dateInput, placeInput;
    private MaterialButton addButton;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rendezvous);

        // Transparent nav bar and colored status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Bind views
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateInput = findViewById(R.id.dateInput);
        placeInput = findViewById(R.id.placeInput);
        addButton = findViewById(R.id.addButton);
        backIcon = findViewById(R.id.backIcon);

        // Handle back
        backIcon.setOnClickListener(v -> finish());

        // Handle form submission
        addButton.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String title = titleInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String place = placeInput.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || place.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enregistrer les données (à implémenter : Firebase, SQLite, etc.)
        Toast.makeText(this, "Rendez-vous ajouté avec succès !", Toast.LENGTH_SHORT).show();
        finish();
    }
}
