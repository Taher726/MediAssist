package com.example.mediassist.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.mediassist.data.models.Medication;

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

        try {
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_MEDICATIONS_TABLE);
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

    public List<Medication> getMedicationsForUser(String userEmail) {
        List<Medication> medications = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Long userId = getUserIdByEmail(userEmail);
            if (userId == null) {
                return medications;
            }

            db = getReadableDatabase();
            String selection = KEY_MEDICATION_USER_ID + " = ?";
            String[] selectionArgs = {userId.toString()};

            cursor = db.query(TABLE_MEDICATIONS, null, selection, selectionArgs, null, null, KEY_MEDICATION_NAME + " ASC");

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

                    int imageIndex = cursor.getColumnIndex(KEY_MEDICATION_IMAGE);
                    if (!cursor.isNull(imageIndex)) {
                        medication.setImageData(cursor.getBlob(imageIndex));
                    }

                    medication.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MEDICATION_IMAGE_PATH)));

                    medications.add(medication);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting medications for user", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
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

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
}