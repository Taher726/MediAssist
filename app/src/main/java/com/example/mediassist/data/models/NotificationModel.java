package com.example.mediassist.data.models;

public class NotificationModel {
    public static final int TYPE_MEDICATION = 1;
    public static final int TYPE_APPOINTMENT = 2;

    private int id;
    private String title;
    private String message;
    private String dateTime;
    private int type;
    private int relatedId; // ID of the related medication or appointment
    private boolean isRead;

    public NotificationModel() {
    }

    public NotificationModel(int id, String title, String message, String dateTime, int type, int relatedId, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.dateTime = dateTime;
        this.type = type;
        this.relatedId = relatedId;
        this.isRead = isRead;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getRelatedId() { return relatedId; }
    public void setRelatedId(int relatedId) { this.relatedId = relatedId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}