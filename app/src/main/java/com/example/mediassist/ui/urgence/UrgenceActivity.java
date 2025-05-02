package com.example.mediassist.ui.urgence;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mediassist.MainActivity;
import com.example.mediassist.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UrgenceActivity extends AppCompatActivity {

    private LinearLayout contactsContainer;
    private TextView emptyContactsMessage;
    private FloatingActionButton addContactButton;
    private ImageButton callSamuButton, callPoliceButton;

    // Removed the ImageView variables for navigation icons that don't exist in the layout
    // private ImageView homeIcon, medicationIcon, renderVousIcon, ordannanceIcon, profileIcon;

    // Sample emergency numbers for Tunisia
    private final String SAMU_NUMBER = "190";
    private final String POLICE_NUMBER = "197";

    // List to hold contacts (in a real app, these would come from a database)
    private List<EmergencyContact> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make navigation bar transparent
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // Make the content display behind navigation bar (immersive mode)
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(flags);

        setContentView(R.layout.activity_urgence);

        // Hide action bar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        initViews();

        // Setup UI listeners
        setupListeners();

        // Load emergency contacts
        loadContacts();

        // Display contacts
        displayContacts();
    }

    private void initViews() {
        contactsContainer = findViewById(R.id.contactsContainer);
        emptyContactsMessage = findViewById(R.id.emptyContactsMessage);
        addContactButton = findViewById(R.id.addContactButton);

        // Removed initialization of navigation icons that aren't in the layout

        // Emergency buttons
        callSamuButton = findViewById(R.id.callSamuBtn);
        callPoliceButton = findViewById(R.id.callPoliceBtn);
    }

    private void setupListeners() {
        // Set up back button
        ImageView backIcon = findViewById(R.id.backIcon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> finish());
        }

        // Set up emergency call buttons
        if (callSamuButton != null) {
            callSamuButton.setOnClickListener(v -> makeEmergencyCall(SAMU_NUMBER));
        }

        if (callPoliceButton != null) {
            callPoliceButton.setOnClickListener(v -> makeEmergencyCall(POLICE_NUMBER));
        }

        // Removed navigation icon click listeners since they don't exist in the layout

        // Set up add contact button
        if (addContactButton != null) {
            addContactButton.setOnClickListener(v -> showAddContactDialog());
        }
    }

    private void loadContacts() {
        // In a real app, you would load contacts from a database
        // For this example, we'll use the sample contacts already in the layout

        // If you want to test the empty state, uncomment the following lines:
        // contacts.clear();
        // contactsContainer.removeAllViews();
    }

    private void displayContacts() {
        // Check if there are contacts to display
        if (contacts.isEmpty() && contactsContainer.getChildCount() == 0) {
            emptyContactsMessage.setVisibility(View.VISIBLE);
        } else {
            emptyContactsMessage.setVisibility(View.GONE);

            // In a real app, you would iterate through contacts and add them dynamically
            // For this example, we're using the static ones from the layout
        }
    }

    private void makeEmergencyCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void showAddContactDialog() {
        // Create a dialog builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        // Inflate your custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_emergency_contact, null);

        // Set the custom view to the dialog
        builder.setView(dialogView);

        // Find views from your custom layout
        TextInputEditText nameInput = dialogView.findViewById(R.id.contactNameInput);
        TextInputEditText relationshipInput = dialogView.findViewById(R.id.contactRelationshipInput);
        TextInputEditText phoneInput = dialogView.findViewById(R.id.contactPhoneInput);
        ImageView imagePreview = dialogView.findViewById(R.id.contactImagePreview);
        Button selectImageButton = dialogView.findViewById(R.id.selectImageButton);

        // Set up dialog buttons
        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            // Handle save logic here
            String name = nameInput.getText().toString();
            String relationship = relationshipInput.getText().toString();
            String phone = phoneInput.getText().toString();

            // Validate inputs and save the contact
            if (!name.isEmpty() && !phone.isEmpty()) {
                // Add your logic to save the contact
                Toast.makeText(this, "Contact enregistré", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        // Set up image selection
        selectImageButton.setOnClickListener(v -> {
            // Implement image selection logic
            Toast.makeText(this, "Sélectionner une photo", Toast.LENGTH_SHORT).show();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateTo(Class<?> destinationClass) {
        Intent intent = new Intent(UrgenceActivity.this, destinationClass);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // Simple class to represent an emergency contact
    private static class EmergencyContact {
        private String name;
        private String relationship;
        private String phoneNumber;

        public EmergencyContact(String name, String relationship, String phoneNumber) {
            this.name = name;
            this.relationship = relationship;
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public String getRelationship() {
            return relationship;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}