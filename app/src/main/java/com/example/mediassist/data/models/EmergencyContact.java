package com.example.mediassist.data.models;

// Update your EmergencyContact class to be serializable
public class EmergencyContact {
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
