package com.example.mediassist.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.mediassist.data.models.Appointment;
import com.example.mediassist.data.models.Medication;
import com.example.mediassist.data.models.NotificationModel;
import com.example.mediassist.data.models.Prescription;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "MediAssistDatabase";
    private static final int DATABASE_VERSION = 6; // Incremented to force fresh DB creation
    private static final String TAG = "DatabaseHelper"; // For logging

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_MEDICATIONS = "medications";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASSWORD = "password";

    // Medication Table Columns
    private static final String KEY_MEDICATION_ID = "id";
    private static final String KEY_MEDICATION_NAME = "name";
    private static final String KEY_MEDICATION_TYPE = "type";
    private static final String KEY_MEDICATION_FREQUENCY = "frequency";
    private static final String KEY_MEDICATION_DOSAGE = "dosage";
    private static final String KEY_MEDICATION_TIME = "time";
    private static final String KEY_MEDICATION_DAYS = "days";
    private static final String KEY_MEDICATION_NOTES = "notes";
    private static final String KEY_MEDICATION_STATUS = "status";
    private static final String KEY_MEDICATION_IMAGE = "image";
    private static final String KEY_MEDICATION_IMAGE_PATH = "image_path";
    private static final String KEY_MEDICATION_USER_ID = "user_id";

    // Add these constants to your DatabaseHelper class
    private static final String TABLE_APPOINTMENTS = "appointments";

    // Appointment Table Columns
    private static final String KEY_APPOINTMENT_ID = "id";
    private static final String KEY_APPOINTMENT_TITLE = "title";
    private static final String KEY_APPOINTMENT_DESCRIPTION = "description";
    private static final String KEY_APPOINTMENT_DATE = "date";
    private static final String KEY_APPOINTMENT_PLACE = "place";
    private static final String KEY_APPOINTMENT_STATUS = "status";
    private static final String KEY_APPOINTMENT_USER_ID = "user_id";

    // Add these constants to your DatabaseHelper class
    private static final String TABLE_PRESCRIPTIONS = "prescriptions";

    // Prescription Table Columns
    private static final String KEY_PRESCRIPTION_ID = "id";
    private static final String KEY_PRESCRIPTION_TITLE = "title";
    private static final String KEY_PRESCRIPTION_DESC = "description";
    private static final String KEY_PRESCRIPTION_FILE_PATH = "file_path";
    private static final String KEY_PRESCRIPTION_FILE_TYPE = "file_type";
    private static final String KEY_PRESCRIPTION_FILE_NAME = "file_name";
    private static final String KEY_PRESCRIPTION_FILE_SIZE = "file_size";
    private static final String KEY_PRESCRIPTION_DATE_ADDED = "date_added";
    private static final String KEY_PRESCRIPTION_USER_ID = "user_id";

    // Constants for notifications table
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_NOTIFICATION_ID = "notification_id";
    private static final String COLUMN_NOTIFICATION_TITLE = "title";
    private static final String COLUMN_NOTIFICATION_MESSAGE = "message";
    private static final String COLUMN_NOTIFICATION_DATETIME = "datetime";
    private static final String COLUMN_NOTIFICATION_TYPE = "type";
    private static final String COLUMN_NOTIFICATION_RELATED_ID = "related_id";
    private static final String COLUMN_NOTIFICATION_IS_READ = "is_read";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_EMAIL + " TEXT UNIQUE," +
                KEY_USER_PASSWORD + " TEXT" +
                ")";

        String CREATE_MEDICATIONS_TABLE = "CREATE TABLE " + TABLE_MEDICATIONS +
                "(" +
                KEY_MEDICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_MEDICATION_NAME + " TEXT," +
                KEY_MEDICATION_TYPE + " TEXT," +
                KEY_MEDICATION_FREQUENCY + " TEXT," +
                KEY_MEDICATION_DOSAGE + " TEXT," +
                KEY_MEDICATION_TIME + " TEXT," +
                KEY_MEDICATION_DAYS + " TEXT," +
                KEY_MEDICATION_NOTES + " TEXT," +
                KEY_MEDICATION_STATUS + " TEXT DEFAULT 'Active'," +
                KEY_MEDICATION_IMAGE + " BLOB," +
                KEY_MEDICATION_IMAGE_PATH + " TEXT," +
                KEY_MEDICATION_USER_ID + " INTEGER," +
                "FOREIGN KEY(" + KEY_MEDICATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" +
                ")";
        // Add this code to create the appointments table
        String CREATE_APPOINTMENTS_TABLE = "CREATE TABLE " + TABLE_APPOINTMENTS +
                "(" +
                KEY_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_APPOINTMENT_TITLE + " TEXT," +
                KEY_APPOINTMENT_DESCRIPTION + " TEXT," +
                KEY_APPOINTMENT_DATE + " TEXT," +
                KEY_APPOINTMENT_PLACE + " TEXT," +
                KEY_APPOINTMENT_STATUS + " TEXT DEFAULT 'Upcoming'," +
                KEY_APPOINTMENT_USER_ID + " INTEGER," +
                "FOREIGN KEY(" + KEY_APPOINTMENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" +
                ")";

        // Add this code to create the prescriptions table
        String CREATE_PRESCRIPTIONS_TABLE = "CREATE TABLE " + TABLE_PRESCRIPTIONS +
                "(" +
                KEY_PRESCRIPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PRESCRIPTION_TITLE + " TEXT," +
                KEY_PRESCRIPTION_DESC + " TEXT," +
                KEY_PRESCRIPTION_FILE_PATH + " TEXT," +
                KEY_PRESCRIPTION_FILE_TYPE + " TEXT," +
                KEY_PRESCRIPTION_FILE_NAME + " TEXT," +
                KEY_PRESCRIPTION_FILE_SIZE + " INTEGER," +
                KEY_PRESCRIPTION_DATE_ADDED + " INTEGER," +
                KEY_PRESCRIPTION_USER_ID + " INTEGER," +
                "FOREIGN KEY(" + KEY_PRESCRIPTION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" +
                ")";

        // Create notifications table
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIFICATION_TITLE + " TEXT,"
                + COLUMN_NOTIFICATION_MESSAGE + " TEXT,"
                + COLUMN_NOTIFICATION_DATETIME + " TEXT,"
                + COLUMN_NOTIFICATION_TYPE + " INTEGER,"
                + COLUMN_NOTIFICATION_RELATED_ID + " INTEGER,"
                + COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0"
                + ")";

        try {
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_MEDICATIONS_TABLE);
            db.execSQL(CREATE_APPOINTMENTS_TABLE);
            // Execute the new prescriptions table creation
            db.execSQL(CREATE_PRESCRIPTIONS_TABLE);
            db.execSQL(CREATE_NOTIFICATIONS_TABLE);
            Log.d(TAG, "Tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables", e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESCRIPTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
            throw e;
        }
    }

    public void resetDatabase() {
        Log.w(TAG, "Resetting entire database - this should only be used during development");
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error resetting database", e);
        } finally {
            db.close();
        }
    }

    // Check if email exists
    public boolean isEmailExists(String email) {
        Log.d(TAG, "Checking if email exists: " + email);
        boolean exists = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT 1 FROM " + TABLE_USERS + " WHERE " + KEY_USER_EMAIL + " = ?",
                    new String[]{email}
            );
            exists = (cursor != null && cursor.getCount() > 0);
            Log.d(TAG, "Email exists: " + exists);
        } catch (Exception e) {
            Log.e(TAG, "Error checking if email exists", e);
        } finally {
            if (cursor != null) cursor.close();
            // We don't close the database here anymore
        }
        return exists;
    }

    // Register user - FIXED DATABASE CONNECTION ISSUE
    public boolean registerUser(String name, String email, String password) {
        Log.d(TAG, "Attempting to register user: " + email);

        // First validate inputs
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            Log.e(TAG, "Invalid registration data - name, email, or password is empty");
            return false;
        }

        SQLiteDatabase db = null;
        boolean success = false;

        try {
            db = getWritableDatabase();
            db.beginTransaction(); // Begin transaction for atomicity

            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, name);
            values.put(KEY_USER_EMAIL, email);
            values.put(KEY_USER_PASSWORD, password);

            long id = db.insert(TABLE_USERS, null, values);
            if (id != -1) {
                db.setTransactionSuccessful();
                success = true;
                Log.d(TAG, "User registered successfully with id: " + id);
            } else {
                Log.e(TAG, "Failed to insert user into database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error registering user", e);
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                    db.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing database", e);
                }
            }
        }
        return success;
    }

    // Check user credentials
    public boolean checkUserCredentials(String email, String password) {
        Log.d(TAG, "Checking credentials for: " + email);
        boolean valid = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            String query = "SELECT 1 FROM " + TABLE_USERS +
                    " WHERE " + KEY_USER_EMAIL + " = ? AND " +
                    KEY_USER_PASSWORD + " = ?";
            cursor = db.rawQuery(query, new String[]{email, password});
            valid = (cursor != null && cursor.getCount() > 0);
            Log.d(TAG, "Credentials valid: " + valid);
        } catch (Exception e) {
            Log.e(TAG, "Error checking user credentials", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return valid;
    }

    // Get user ID by email
    public Long getUserIdByEmail(String email) {
        Long userId = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            String query = "SELECT " + KEY_USER_ID + " FROM " + TABLE_USERS +
                    " WHERE " + KEY_USER_EMAIL + " = ?";
            cursor = db.rawQuery(query, new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user ID by email", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return userId;
    }

    // Get username by email
    public String getUserNameByEmail(String email) {
        String userName = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            String query = "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USERS +
                    " WHERE " + KEY_USER_EMAIL + " = ?";
            cursor = db.rawQuery(query, new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                userName = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user name by email", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return userName;
    }

    // Medication Management Methods
    public long addMedication(Medication medication, String userEmail) {
        long medicationId = -1;
        SQLiteDatabase db = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User not found with email: " + userEmail);
                return -1;
            }

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_MEDICATION_NAME, medication.getName());
            values.put(KEY_MEDICATION_TYPE, medication.getType());
            values.put(KEY_MEDICATION_FREQUENCY, medication.getFrequency());
            values.put(KEY_MEDICATION_DOSAGE, medication.getDosage());
            values.put(KEY_MEDICATION_TIME, medication.getTime());
            values.put(KEY_MEDICATION_DAYS, medication.getDays());
            values.put(KEY_MEDICATION_NOTES, medication.getNotes());
            values.put(KEY_MEDICATION_STATUS, medication.getStatus());
            values.put(KEY_MEDICATION_USER_ID, userId);

            if (medication.getImageData() != null) {
                values.put(KEY_MEDICATION_IMAGE, medication.getImageData());
            }
            values.put(KEY_MEDICATION_IMAGE_PATH, medication.getImagePath());

            medicationId = db.insert(TABLE_MEDICATIONS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding medication", e);
        } finally {
            if (db != null) db.close();
        }
        return medicationId;
    }

    // Modified getMedicationsForUser method with improved logging
    public List<Medication> getMedicationsForUser(String userEmail) {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Log.d(TAG, "Finding medications for email: " + userEmail);

            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User ID not found for email: " + userEmail);
                return medications;
            }

            Log.d(TAG, "Found user ID: " + userId);

            db = getReadableDatabase();

            // First, get basic medication data without the image blob
            String[] columns = {
                    KEY_MEDICATION_ID,
                    KEY_MEDICATION_NAME,
                    KEY_MEDICATION_TYPE,
                    KEY_MEDICATION_FREQUENCY,
                    KEY_MEDICATION_DOSAGE,
                    KEY_MEDICATION_TIME,
                    KEY_MEDICATION_DAYS,
                    KEY_MEDICATION_NOTES,
                    KEY_MEDICATION_STATUS,
                    KEY_MEDICATION_IMAGE_PATH
            };

            String selection = KEY_MEDICATION_USER_ID + " = ?";
            String[] selectionArgs = {userId.toString()};

            cursor = db.query(
                    TABLE_MEDICATIONS,    // table
                    columns,              // columns (without the image data)
                    selection,            // selection
                    selectionArgs,        // selectionArgs
                    null,                 // groupBy
                    null,                 // having
                    KEY_MEDICATION_NAME + " ASC"  // orderBy
            );

            Log.d(TAG, "Query executed, cursor has " + (cursor != null ? cursor.getCount() : 0) + " results");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Medication medication = new Medication();
                    medication.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MEDICATION_ID)));
                    medication.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_NAME)));
                    medication.setType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_TYPE)));
                    medication.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_FREQUENCY)));
                    medication.setDosage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_DOSAGE)));
                    medication.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_TIME)));
                    medication.setDays(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_DAYS)));
                    medication.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_NOTES)));
                    medication.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_STATUS)));
                    medication.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_IMAGE_PATH)));

                    // Now get the image data separately if needed
                    // We'll only do this for display if required
                /*
                Cursor imageCursor = null;
                try {
                    String[] imgColumns = {KEY_MEDICATION_IMAGE};
                    String imgSelection = KEY_MEDICATION_ID + " = ?";
                    String[] imgSelectionArgs = {String.valueOf(medication.getId())};

                    imageCursor = db.query(
                        TABLE_MEDICATIONS,
                        imgColumns,
                        imgSelection,
                        imgSelectionArgs,
                        null, null, null
                    );

                    if (imageCursor != null && imageCursor.moveToFirst()) {
                        int imageIndex = imageCursor.getColumnIndex(KEY_MEDICATION_IMAGE);
                        if (!imageCursor.isNull(imageIndex)) {
                            medication.setImageData(imageCursor.getBlob(imageIndex));
                        }
                    }
                } finally {
                    if (imageCursor != null) {
                        imageCursor.close();
                    }
                }
                */

                    Log.d(TAG, "Added medication: " + medication.getName());
                    medications.add(medication);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting medications for user", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        Log.d(TAG, "Returning " + medications.size() + " medications");
        return medications;
    }

    public boolean deleteMedication(int medicationId) {
        boolean success = false;
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            int rowsDeleted = db.delete(TABLE_MEDICATIONS, KEY_MEDICATION_ID + " = ?",
                    new String[]{String.valueOf(medicationId)});
            success = rowsDeleted > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting medication", e);
        } finally {
            if (db != null) db.close();
        }
        return success;
    }

    // Add this method to the DatabaseHelper class
    public long updateMedication(Medication medication, String userEmail) {
        long result = -1;
        SQLiteDatabase db = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User not found with email: " + userEmail);
                return -1;
            }

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_MEDICATION_NAME, medication.getName());
            values.put(KEY_MEDICATION_TYPE, medication.getType());
            values.put(KEY_MEDICATION_FREQUENCY, medication.getFrequency());
            values.put(KEY_MEDICATION_DOSAGE, medication.getDosage());
            values.put(KEY_MEDICATION_TIME, medication.getTime());
            values.put(KEY_MEDICATION_DAYS, medication.getDays());
            values.put(KEY_MEDICATION_NOTES, medication.getNotes());
            values.put(KEY_MEDICATION_STATUS, medication.getStatus());

            if (medication.getImageData() != null) {
                values.put(KEY_MEDICATION_IMAGE, medication.getImageData());
            }
            values.put(KEY_MEDICATION_IMAGE_PATH, medication.getImagePath());

            result = db.update(TABLE_MEDICATIONS, values,
                    KEY_MEDICATION_ID + " = ? AND " + KEY_MEDICATION_USER_ID + " = ?",
                    new String[]{String.valueOf(medication.getId()), String.valueOf(userId)});
        } catch (Exception e) {
            Log.e(TAG, "Error updating medication", e);
        } finally {
            if (db != null) db.close();
        }
        return result;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

    public byte[] getMedicationImage(int medicationId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        byte[] imageData = null;

        try {
            db = getReadableDatabase();
            String[] columns = {KEY_MEDICATION_IMAGE};
            String selection = KEY_MEDICATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(medicationId)};

            cursor = db.query(
                    TABLE_MEDICATIONS,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int imageIndex = cursor.getColumnIndex(KEY_MEDICATION_IMAGE);
                if (!cursor.isNull(imageIndex)) {
                    imageData = cursor.getBlob(imageIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting medication image", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return imageData;
    }

    // Add appointment methods
    public long addAppointment(Appointment appointment, String userEmail) {
        long appointmentId = -1;
        SQLiteDatabase db = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User not found with email: " + userEmail);
                return -1;
            }

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_APPOINTMENT_TITLE, appointment.getTitle());
            values.put(KEY_APPOINTMENT_DESCRIPTION, appointment.getDescription());
            values.put(KEY_APPOINTMENT_DATE, appointment.getDate());
            values.put(KEY_APPOINTMENT_PLACE, appointment.getPlace());
            values.put(KEY_APPOINTMENT_STATUS, appointment.getStatus());
            values.put(KEY_APPOINTMENT_USER_ID, userId);

            appointmentId = db.insert(TABLE_APPOINTMENTS, null, values);
            Log.d(TAG, "Added appointment with ID: " + appointmentId);
        } catch (Exception e) {
            Log.e(TAG, "Error adding appointment", e);
        } finally {
            if (db != null) db.close();
        }
        return appointmentId;
    }

    public List<Appointment> getAppointmentsForUser(String userEmail) {
        List<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User ID not found for email: " + userEmail);
                return appointments;
            }

            db = getReadableDatabase();
            String selection = KEY_APPOINTMENT_USER_ID + " = ?";
            String[] selectionArgs = {userId.toString()};
            String orderBy = KEY_APPOINTMENT_DATE + " ASC";

            cursor = db.query(
                    TABLE_APPOINTMENTS,    // table
                    null,                  // columns (null = all)
                    selection,             // selection
                    selectionArgs,         // selectionArgs
                    null,                  // groupBy
                    null,                  // having
                    orderBy                // orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Appointment appointment = new Appointment();
                    appointment.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_ID)));
                    appointment.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_TITLE)));
                    appointment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_DESCRIPTION)));
                    appointment.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_DATE)));
                    appointment.setPlace(cursor.getString(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_PLACE)));
                    appointment.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_STATUS)));
                    appointment.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_APPOINTMENT_USER_ID)));

                    appointments.add(appointment);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting appointments for user", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return appointments;
    }

    public boolean deleteAppointment(int appointmentId) {
        boolean success = false;
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            success = db.delete(TABLE_APPOINTMENTS, KEY_APPOINTMENT_ID + " = ?",
                    new String[]{String.valueOf(appointmentId)}) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting appointment", e);
        } finally {
            if (db != null) db.close();
        }

        return success;
    }

    public boolean updateAppointment(Appointment appointment, String userEmail) {
        boolean success = false;
        SQLiteDatabase db = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User not found with email: " + userEmail);
                return false;
            }

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_APPOINTMENT_TITLE, appointment.getTitle());
            values.put(KEY_APPOINTMENT_DESCRIPTION, appointment.getDescription());
            values.put(KEY_APPOINTMENT_DATE, appointment.getDate());
            values.put(KEY_APPOINTMENT_PLACE, appointment.getPlace());
            values.put(KEY_APPOINTMENT_STATUS, appointment.getStatus());

            success = db.update(TABLE_APPOINTMENTS, values,
                    KEY_APPOINTMENT_ID + " = ? AND " + KEY_APPOINTMENT_USER_ID + " = ?",
                    new String[]{String.valueOf(appointment.getId()), userId.toString()}) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating appointment", e);
        } finally {
            if (db != null) db.close();
        }

        return success;
    }

    // Add prescription methods
    public long addPrescription(Prescription prescription, String userEmail) {
        long prescriptionId = -1;
        SQLiteDatabase db = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User not found with email: " + userEmail);
                return -1;
            }

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_PRESCRIPTION_TITLE, prescription.getTitle());
            values.put(KEY_PRESCRIPTION_DESC, prescription.getDescription());
            values.put(KEY_PRESCRIPTION_FILE_PATH, prescription.getFilePath());
            values.put(KEY_PRESCRIPTION_FILE_TYPE, prescription.getFileType());
            values.put(KEY_PRESCRIPTION_FILE_NAME, prescription.getFileName());
            values.put(KEY_PRESCRIPTION_FILE_SIZE, prescription.getFileSize());
            values.put(KEY_PRESCRIPTION_DATE_ADDED, prescription.getDateAdded());
            values.put(KEY_PRESCRIPTION_USER_ID, userId);

            prescriptionId = db.insert(TABLE_PRESCRIPTIONS, null, values);
            Log.d(TAG, "Added prescription with ID: " + prescriptionId);
        } catch (Exception e) {
            Log.e(TAG, "Error adding prescription", e);
        } finally {
            if (db != null) db.close();
        }
        return prescriptionId;
    }

    public List<Prescription> getPrescriptionsForUser(String userEmail) {
        List<Prescription> prescriptions = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User ID not found for email: " + userEmail);
                return prescriptions;
            }

            db = getReadableDatabase();
            String selection = KEY_PRESCRIPTION_USER_ID + " = ?";
            String[] selectionArgs = {userId.toString()};
            String orderBy = KEY_PRESCRIPTION_DATE_ADDED + " DESC"; // Most recent first

            cursor = db.query(
                    TABLE_PRESCRIPTIONS,  // table
                    null,                 // columns (null = all)
                    selection,            // selection
                    selectionArgs,        // selectionArgs
                    null,                 // groupBy
                    null,                 // having
                    orderBy               // orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Prescription prescription = new Prescription();
                    prescription.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_ID)));
                    prescription.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_TITLE)));
                    prescription.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_DESC)));
                    prescription.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_PATH)));
                    prescription.setFileType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_TYPE)));
                    prescription.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_NAME)));
                    prescription.setFileSize(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_SIZE)));
                    prescription.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_DATE_ADDED)));
                    prescription.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_USER_ID)));

                    prescriptions.add(prescription);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting prescriptions for user", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return prescriptions;
    }

    public List<Prescription> searchPrescriptions(String query, String userEmail) {
        List<Prescription> prescriptions = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                Log.e(TAG, "User ID not found for email: " + userEmail);
                return prescriptions;
            }

            db = getReadableDatabase();
            String selection = KEY_PRESCRIPTION_USER_ID + " = ? AND (" +
                    KEY_PRESCRIPTION_TITLE + " LIKE ? OR " +
                    KEY_PRESCRIPTION_DESC + " LIKE ? OR " +
                    KEY_PRESCRIPTION_FILE_NAME + " LIKE ?)";

            String[] selectionArgs = {
                    userId.toString(),
                    "%" + query + "%",
                    "%" + query + "%",
                    "%" + query + "%"
            };

            String orderBy = KEY_PRESCRIPTION_DATE_ADDED + " DESC";

            cursor = db.query(
                    TABLE_PRESCRIPTIONS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Prescription prescription = new Prescription();
                    prescription.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_ID)));
                    prescription.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_TITLE)));
                    prescription.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_DESC)));
                    prescription.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_PATH)));
                    prescription.setFileType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_TYPE)));
                    prescription.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_NAME)));
                    prescription.setFileSize(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_SIZE)));
                    prescription.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_DATE_ADDED)));
                    prescription.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_USER_ID)));

                    prescriptions.add(prescription);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching prescriptions", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return prescriptions;
    }

    public boolean deletePrescription(int prescriptionId) {
        boolean success = false;
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            success = db.delete(TABLE_PRESCRIPTIONS, KEY_PRESCRIPTION_ID + " = ?",
                    new String[]{String.valueOf(prescriptionId)}) > 0;

            Log.d(TAG, success ? "Prescription deleted successfully" : "Failed to delete prescription");
        } catch (Exception e) {
            Log.e(TAG, "Error deleting prescription", e);
        } finally {
            if (db != null) db.close();
        }

        return success;
    }

    public Prescription getPrescriptionById(int prescriptionId) {
        Prescription prescription = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            String selection = KEY_PRESCRIPTION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(prescriptionId)};

            cursor = db.query(
                    TABLE_PRESCRIPTIONS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                prescription = new Prescription();
                prescription.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_ID)));
                prescription.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_TITLE)));
                prescription.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_DESC)));
                prescription.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_PATH)));
                prescription.setFileType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_TYPE)));
                prescription.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_NAME)));
                prescription.setFileSize(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_FILE_SIZE)));
                prescription.setDateAdded(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_DATE_ADDED)));
                prescription.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRESCRIPTION_USER_ID)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting prescription by ID", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return prescription;
    }

    /**
     * Count medications for a specific user
     */
    public int getMedicationCount(String userEmail) {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                return 0;
            }

            db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + TABLE_MEDICATIONS +
                    " WHERE " + KEY_MEDICATION_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{userId.toString()});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting medications", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    /**
     * Count appointments for a specific user
     */
    public int getAppointmentCount(String userEmail) {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                return 0;
            }

            db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS +
                    " WHERE " + KEY_APPOINTMENT_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{userId.toString()});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting appointments", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    /**
     * Count prescriptions for a specific user
     */
    public int getPrescriptionCount(String userEmail) {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                return 0;
            }

            db = getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + TABLE_PRESCRIPTIONS +
                    " WHERE " + KEY_PRESCRIPTION_USER_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{userId.toString()});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting prescriptions", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    // Method to add a new notification
    public long addNotification(NotificationModel notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NOTIFICATION_TITLE, notification.getTitle());
        values.put(COLUMN_NOTIFICATION_MESSAGE, notification.getMessage());
        values.put(COLUMN_NOTIFICATION_DATETIME, notification.getDateTime());
        values.put(COLUMN_NOTIFICATION_TYPE, notification.getType());
        values.put(COLUMN_NOTIFICATION_RELATED_ID, notification.getRelatedId());
        values.put(COLUMN_NOTIFICATION_IS_READ, notification.isRead() ? 1 : 0);

        long id = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return id;
    }

    // Method to get all notifications
    public List<NotificationModel> getAllNotifications() {
        List<NotificationModel> notificationsList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " +
                COLUMN_NOTIFICATION_DATETIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NotificationModel notification = new NotificationModel();
                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID)));
                notification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TITLE)));
                notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_MESSAGE)));
                notification.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_DATETIME)));
                notification.setType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TYPE)));
                notification.setRelatedId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_RELATED_ID)));
                notification.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_IS_READ)) == 1);

                notificationsList.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notificationsList;
    }

    // Method to mark notification as read
    public int markNotificationAsRead(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_IS_READ, 1);

        return db.update(TABLE_NOTIFICATIONS, values, COLUMN_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Method to delete a notification
    public void deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, COLUMN_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Method to get unread notification count
    // Method to get unread notification count
    public int getUnreadNotificationsCount() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            String countQuery = "SELECT COUNT(*) FROM notifications WHERE is_read = 0";
            cursor = db.rawQuery(countQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If the table doesn't exist yet, return 0
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return count;
    }
}