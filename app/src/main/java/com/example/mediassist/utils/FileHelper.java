package com.example.mediassist.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileHelper {
    private static final String TAG = "FileHelper";

    // Copy a file from a Uri to the app's private storage
    public static String saveFileToAppStorage(Context context, Uri uri) {
        if (uri == null) return null;

        String fileName = getFileName(context, uri);
        String fileExtension = getFileExtension(context, uri);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Create a unique filename
        if (fileName == null) {
            fileName = "file_" + timeStamp + (fileExtension != null ? "." + fileExtension : "");
        }

        // Get app's private directory
        File storageDir = new File(context.getFilesDir(), "prescriptions");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File destinationFile = new File(storageDir, fileName);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving file", e);
            return null;
        }
    }

    // Get file name from Uri
    public static String getFileName(Context context, Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name", e);
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    // Get file size from Uri
    public static long getFileSize(Context context, Uri uri) {
        long size = 0;

        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    size = cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file size", e);
        }

        return size;
    }

    // Get file extension from Uri
    public static String getFileExtension(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Get file type (pdf, image) from Uri
    public static String getFileType(Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);

        if (mimeType == null) return null;

        if (mimeType.contains("pdf")) {
            return "pdf";
        } else if (mimeType.contains("image")) {
            return "image";
        } else {
            return "other";
        }
    }

    // Delete file from storage
    public static boolean deleteFile(String filePath) {
        if (filePath == null) return false;

        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}