package com.example.mediassist.ui.urgence;

import android.content.ActivityNotFoundException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UrgenceActivity extends AppCompatActivity {

    private LinearLayout contactsContainer;
    private TextView emptyContactsMessage;
    private FloatingActionButton addContactButton;
    private ImageButton callSamuButton, callPoliceButton;

    // Storage preferences constants
    private static final String PREFS_NAME = "EmergencyContactsPrefs";
    private static final String CONTACTS_KEY = "emergency_contacts";

    // Sample emergency numbers for Tunisia
    private final String SAMU_NUMBER = "190";
    private final String POLICE_NUMBER = "197";

    // List to hold contacts
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

        // Load emergency contacts (this must come before setupListeners)
        loadContacts();

        // Setup UI listeners
        setupListeners();
    }

    private void initViews() {
        contactsContainer = findViewById(R.id.contactsContainer);
        emptyContactsMessage = findViewById(R.id.emptyContactsMessage);
        addContactButton = findViewById(R.id.addContactButton);

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

        // Find the predefined emergency numbers
        TextView samuNumberText = null;
        TextView policeNumberText = null;

        // Find the emergency info card
        CardView emergencyCard = findViewById(R.id.emergencyInfo);
        if (emergencyCard != null) {
            // Get the main linear layout
            LinearLayout mainLayout = (LinearLayout) emergencyCard.getChildAt(0);

            // Get the SAMU container (index 1 because title is index 0)
            LinearLayout samuContainer = (LinearLayout) mainLayout.getChildAt(1);
            if (samuContainer != null) {
                // SAMU info is in the middle LinearLayout
                LinearLayout samuInfo = (LinearLayout) samuContainer.getChildAt(1);
                if (samuInfo != null && samuInfo.getChildCount() >= 2) {
                    // The second child is the phone number
                    samuNumberText = (TextView) samuInfo.getChildAt(1);
                }
            }

            // Get the Police container (index 2)
            LinearLayout policeContainer = (LinearLayout) mainLayout.getChildAt(2);
            if (policeContainer != null) {
                // Police info is in the middle LinearLayout
                LinearLayout policeInfo = (LinearLayout) policeContainer.getChildAt(1);
                if (policeInfo != null && policeInfo.getChildCount() >= 2) {
                    // The second child is the phone number
                    policeNumberText = (TextView) policeInfo.getChildAt(1);
                }
            }
        }

        // Set up emergency call buttons
        if (callSamuButton != null) {
            callSamuButton.setOnClickListener(v -> makeEmergencyCall(SAMU_NUMBER));
        }

        if (callPoliceButton != null) {
            callPoliceButton.setOnClickListener(v -> makeEmergencyCall(POLICE_NUMBER));
        }

        // Make the SAMU phone number text clickable too
        if (samuNumberText != null) {
            samuNumberText.setOnClickListener(v -> makeEmergencyCall(SAMU_NUMBER));
        }

        // Make the Police phone number text clickable too
        if (policeNumberText != null) {
            policeNumberText.setOnClickListener(v -> makeEmergencyCall(POLICE_NUMBER));
        }

        // Set up add contact button
        if (addContactButton != null) {
            addContactButton.setOnClickListener(v -> showAddContactDialog());
        }

        // Set up click listeners for existing personal contacts in the layout
        setupExistingContactsListeners();
    }

    /**
     * Set up click listeners for the existing emergency contacts in the layout
     */
    private void setupExistingContactsListeners() {
        // Find all the contact cards in the contactsContainer
        for (int i = 0; i < contactsContainer.getChildCount(); i++) {
            View cardView = contactsContainer.getChildAt(i);
            if (cardView instanceof CardView) {
                // Process each CardView (emergency contact)
                setupContactCardListeners((CardView) cardView);
            }
        }
    }

    /**
     * Set up click listeners for a specific contact card
     */
    private void setupContactCardListeners(CardView cardView) {
        try {
            // Find the phone TextView
            TextView phoneText = null;
            LinearLayout mainLayout = (LinearLayout) cardView.getChildAt(0);
            if (mainLayout != null) {
                LinearLayout infoLayout = (LinearLayout) ((LinearLayout) mainLayout).getChildAt(1);
                if (infoLayout != null && infoLayout.getChildCount() >= 3) {
                    phoneText = (TextView) infoLayout.getChildAt(2); // Third item is phone number
                }

                // Find the call button
                ImageButton callButton = (ImageButton) ((LinearLayout) mainLayout).getChildAt(2);

                // Get the phone number
                final String phoneNumber = phoneText != null ? phoneText.getText().toString() : null;

                // Set click listeners if phone number is found
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    // Make the phone text clickable
                    phoneText.setOnClickListener(v -> makeEmergencyCall(phoneNumber));

                    // Make the call button clickable
                    if (callButton != null) {
                        callButton.setOnClickListener(v -> makeEmergencyCall(phoneNumber));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log any errors but don't crash
        }
    }

    private void loadContacts() {
        // Load contacts from storage
        loadContactsFromStorage();

        // Clear existing contact views (except default examples if you want to keep them)
        contactsContainer.removeAllViews();

        // Add contacts from the list
        for (EmergencyContact contact : contacts) {
            addContactCard(contact);
        }

        // Update empty state visibility
        if (contacts.isEmpty() && contactsContainer.getChildCount() == 0) {
            emptyContactsMessage.setVisibility(View.VISIBLE);
        } else {
            emptyContactsMessage.setVisibility(View.GONE);
        }
    }

    /**
     * Load all contacts from SharedPreferences
     */
    private void loadContactsFromStorage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedContacts = prefs.getStringSet(CONTACTS_KEY, new HashSet<>());

        // Clear existing contacts
        contacts.clear();

        // Deserialize and add to contacts list
        for (String contactStr : savedContacts) {
            EmergencyContact contact = EmergencyContact.deserialize(contactStr);
            if (contact != null) {
                contacts.add(contact);
            }
        }
    }

    /**
     * Save all contacts to SharedPreferences
     */
    private void saveContacts() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Convert contacts to strings
        ArrayList<String> serializedContacts = new ArrayList<>();
        for (EmergencyContact contact : contacts) {
            serializedContacts.add(contact.serialize());
        }

        // Save as string set
        editor.putStringSet(CONTACTS_KEY, new HashSet<>(serializedContacts));
        editor.apply();
    }

    /**
     * Delete a contact at specific position
     */
    private void deleteContact(int position) {
        if (position >= 0 && position < contacts.size()) {
            contacts.remove(position);
            saveContacts();
            reloadContacts();
        }
    }

    /**
     * Reload all contacts from the contacts list into the UI
     */
    private void reloadContacts() {
        // Clear existing contacts from UI
        contactsContainer.removeAllViews();

        // Add all contacts back to the UI
        for (EmergencyContact contact : contacts) {
            addContactCard(contact);
        }

        // Show empty state if no contacts
        if (contacts.isEmpty()) {
            emptyContactsMessage.setVisibility(View.VISIBLE);
        } else {
            emptyContactsMessage.setVisibility(View.GONE);
        }
    }

    /**
     * Opens the phone dialer with the specified phone number
     */
    private void makeEmergencyCall(String phoneNumber) {
        try {
            // Clean the phone number
            String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");

            // Create the URI
            Uri uri = Uri.parse("tel:" + cleanNumber);

            // Create a more general intent that might be handled by more apps
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            // Start activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Specific error handling for no activity found
            Toast.makeText(this, "Aucune application pour passer des appels", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            // General error handling
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Adds a new contact card to the UI with proper click listeners
     */
    private void addContactCard(final EmergencyContact contact) {
        View cardView = getLayoutInflater().inflate(R.layout.item_emergency_contact, null);

        // Find views in the inflated layout
        TextView nameText = cardView.findViewById(R.id.contactName);
        TextView relationText = cardView.findViewById(R.id.contactRelation);
        TextView phoneText = cardView.findViewById(R.id.contactPhone);
        ImageButton callButton = cardView.findViewById(R.id.callContactBtn);

        // Set contact data
        nameText.setText(contact.getName());
        relationText.setText(contact.getRelationship());
        phoneText.setText(contact.getPhoneNumber());

        // Set click listeners for phone number and call button
        phoneText.setOnClickListener(v -> makeEmergencyCall(contact.getPhoneNumber()));
        callButton.setOnClickListener(v -> makeEmergencyCall(contact.getPhoneNumber()));

        // Set long-press listener for deletion
        cardView.setOnLongClickListener(v -> {
            // Show delete confirmation
            new AlertDialog.Builder(UrgenceActivity.this)
                    .setTitle("Supprimer le contact")
                    .setMessage("Êtes-vous sûr de vouloir supprimer ce contact ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Find the contact position and delete it
                        int position = contacts.indexOf(contact);
                        if (position != -1) {
                            deleteContact(position);
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
            return true;
        });

        // Add to container
        contactsContainer.addView(cardView);

        // Update empty state visibility
        emptyContactsMessage.setVisibility(View.GONE);
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

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Set up dialog buttons - use dialog view instead of builder to avoid dismiss after click
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            // Handle save logic here
            String name = nameInput.getText().toString();
            String relationship = relationshipInput.getText().toString();
            String phone = phoneInput.getText().toString();

            // Validate inputs and save the contact
            if (!name.isEmpty() && !phone.isEmpty()) {
                // Create a new contact and add it to the list
                EmergencyContact newContact = new EmergencyContact(name, relationship, phone);
                contacts.add(newContact);

                // Add the contact to the UI
                addContactCard(newContact);

                // Save contacts to storage
                saveContacts();

                Toast.makeText(this, "Contact enregistré", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up image selection
        selectImageButton.setOnClickListener(v -> {
            // Implement image selection logic
            Toast.makeText(this, "Sélectionner une photo", Toast.LENGTH_SHORT).show();
        });

        // Show the dialog
        dialog.show();
    }

    // Simple class to represent an emergency contact with serialization support
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

        // Methods for serializing/deserializing contacts for storage
        public String serialize() {
            // Simple serialization: name|relationship|phoneNumber
            return name + "|" + relationship + "|" + phoneNumber;
        }

        public static EmergencyContact deserialize(String serialized) {
            String[] parts = serialized.split("\\|", 3);
            if (parts.length == 3) {
                return new EmergencyContact(parts[0], parts[1], parts[2]);
            }
            return null;
        }
    }
}