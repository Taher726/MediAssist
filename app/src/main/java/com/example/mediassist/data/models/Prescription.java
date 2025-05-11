package com.example.mediassist.data.models;

public class Prescription {
    private int id;
    private String title;
    private String description;
    private String filePath; // Path to the file (PDF or image)
    private String fileType; // "pdf" or "image"
    private String fileName; // Original file name
    private long fileSize; // Size in bytes
    private long dateAdded; // Timestamp when added
    private int userId;

    public Prescription() {
        // Default constructor
        this.dateAdded = System.currentTimeMillis();
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Helper method to check if the file is a PDF
    public boolean isPdf() {
        return "pdf".equals(fileType);
    }

    // Helper method to check if the file is an image
    public boolean isImage() {
        return "image".equals(fileType);
    }

    // Helper method to get a human-readable file size
    public String getReadableFileSize() {
        if (fileSize <= 0) return "0 B";

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));

        return String.format("%.1f %s", fileSize / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}