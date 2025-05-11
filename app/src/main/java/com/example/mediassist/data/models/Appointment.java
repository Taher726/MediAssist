package com.example.mediassist.data.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
    private int id;
    private String title;
    private String description;
    private String date; // Store as string in format "yyyy-MM-dd HH:mm"
    private String place;
    private int userId;
    private String status; // e.g., "Upcoming", "Completed", "Cancelled"

    public Appointment() {
        // Default constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper methods for date formatting
    public String getFormattedDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE d MMMM, HH:mm", Locale.FRENCH);
            Date date = inputFormat.parse(this.date);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return this.date; // Return original if parsing fails
        }
    }

    public boolean isUpcoming() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date appointmentDate = format.parse(this.date);
            Date now = new Date();
            return appointmentDate.after(now);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}