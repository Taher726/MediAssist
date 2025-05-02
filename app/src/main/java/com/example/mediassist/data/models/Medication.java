package com.example.mediassist.data.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medication {
    private int id;
    private String name;
    private String type;
    private String frequency;
    private String dosage;
    private String time;
    private String days;
    private String notes;
    private String status;
    private byte[] imageData;
    private String imagePath;

    public Medication() {
        // Default constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Helper methods to format times and days for display
    public String getFormattedTimes() {
        if (time == null || time.isEmpty()) {
            return "Not set";
        }
        return time.replace(",", ", ");
    }

    public String getFormattedDays() {
        if (days == null || days.isEmpty()) {
            return "Not set";
        }
        return days.replace(",", ", ");
    }

    // Get times as a list
    public List<String> getTimesList() {
        if (time == null || time.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(time.split(","));
    }

    // Get days as a list
    public List<String> getDaysList() {
        if (days == null || days.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(days.split(","));
    }
}