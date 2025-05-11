package com.example.mediassist.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.models.NotificationModel;
import com.example.mediassist.receivers.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationService {
    private static final String CHANNEL_ID = "mediassist_channel";
    private static final String CHANNEL_NAME = "MediAssist Notifications";
    private static final String CHANNEL_DESC = "Notifications for medications and appointments";

    private Context context;
    private DatabaseHelper dbHelper;

    public NotificationService(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
        createNotificationChannel();
    }

    // Create notification channel for Android O and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Schedule a notification for a medication
    public void scheduleMedicationNotification(int medicationId, String medicationName,
                                               String dosage, String time, String date) {

        // Create a calendar instance for the scheduled time
        Calendar calendar = Calendar.getInstance();
        try {
            // Parse time (HH:mm)
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Parse date (yyyy-MM-dd)
            String[] dateParts = date.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
            int day = Integer.parseInt(dateParts[2]);

            calendar.set(year, month, day, hour, minute, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Only schedule if the time is in the future
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            return;
        }

        // Create intent for the notification receiver
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("type", NotificationModel.TYPE_MEDICATION);
        intent.putExtra("title", "Rappel de médicament");
        intent.putExtra("message", "Il est temps de prendre " + medicationName + " (" + dosage + ")");
        intent.putExtra("medicationId", medicationId);

        // Create unique request code based on medication ID and time
        int requestCode = (medicationId * 100) + calendar.get(Calendar.HOUR_OF_DAY);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Get alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        }

        // Save notification to database
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        NotificationModel notification = new NotificationModel();
        notification.setTitle("Rappel de médicament");
        notification.setMessage("Programmé: " + medicationName + " (" + dosage + ") à " + time);
        notification.setDateTime(currentDateTime);
        notification.setType(NotificationModel.TYPE_MEDICATION);
        notification.setRelatedId(medicationId);
        notification.setRead(false);

        dbHelper.addNotification(notification);
    }

    // Schedule a notification for an appointment
    // Update the scheduleAppointmentNotification method in NotificationService.java

    public void scheduleAppointmentNotification(int appointmentId, String doctorName,
                                                String location, String date, String time) {

        try {
            Log.d("NotificationService", "Scheduling appointment notification: " +
                    appointmentId + ", " + doctorName + ", " + date + " " + time);

            // Create a calendar instance for the scheduled time
            Calendar appointmentTime = Calendar.getInstance();

            // Parse time (HH:mm)
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Parse date (yyyy-MM-dd)
            String[] dateParts = date.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
            int day = Integer.parseInt(dateParts[2]);

            appointmentTime.set(year, month, day, hour, minute, 0);

            // Get current time
            Calendar now = Calendar.getInstance();

            // Debug logs
            Log.d("NotificationService", "Current time: " + now.getTime());
            Log.d("NotificationService", "Appointment time: " + appointmentTime.getTime());

            // Calculate notification time (1 hour before appointment)
            Calendar notificationTime = (Calendar) appointmentTime.clone();
            notificationTime.add(Calendar.HOUR, -1);

            Log.d("NotificationService", "Notification time (1h before): " + notificationTime.getTime());

            // Check if the notification time is in the future
            if (notificationTime.getTimeInMillis() <= now.getTimeInMillis()) {
                Log.d("NotificationService", "Appointment is too soon or in the past, showing immediate notification");

                // The appointment is less than 1 hour away or in the past
                // Show a notification immediately if the appointment is still in the future
                if (appointmentTime.getTimeInMillis() > now.getTimeInMillis()) {
                    // Show immediate notification
                    String message = "Rendez-vous imminent avec Dr. " + doctorName + " à " +
                            time + " à " + location;
                    showNotification("Rappel de rendez-vous", message, 500000 + appointmentId);
                    return;
                } else {
                    Log.d("NotificationService", "Appointment is in the past, not scheduling");
                    return;
                }
            }

            // Create intent for the notification receiver
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("type", NotificationModel.TYPE_APPOINTMENT);
            intent.putExtra("title", "Rappel de rendez-vous");
            intent.putExtra("message", "Rendez-vous avec Dr. " + doctorName + " à " +
                    time + " à " + location);
            intent.putExtra("appointmentId", appointmentId);

            // Create unique request code based on appointment ID
            int requestCode = 500000 + appointmentId;

            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            // Get alarm manager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Set the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !alarmManager.canScheduleExactAlarms()) {
                // For Android 12+, need SCHEDULE_EXACT_ALARM permission
                Log.w("NotificationService", "Cannot schedule exact alarms, using inexact alarm");
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        notificationTime.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        notificationTime.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        notificationTime.getTimeInMillis(), pendingIntent);
            }

            // Log success
            Log.d("NotificationService", "Alarm scheduled for: " + notificationTime.getTime());

            // Save notification to database
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());

            NotificationModel notification = new NotificationModel();
            notification.setTitle("Rappel de rendez-vous");
            notification.setMessage("Programmé: Dr. " + doctorName + " à " + time + " à " + location);
            notification.setDateTime(currentDateTime);
            notification.setType(NotificationModel.TYPE_APPOINTMENT);
            notification.setRelatedId(appointmentId);
            notification.setRead(false);

            dbHelper.addNotification(notification);

        } catch (Exception e) {
            Log.e("NotificationService", "Error scheduling appointment notification", e);
        }
    }

    // Display a notification
    // Display a notification
    public void showNotification(String title, String message, int notificationId) {
        // Create intent for notification tap action
        Intent intent = new Intent(context, com.example.mediassist.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.stethoscope)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Use context instead of this for permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // You can't request permissions from a non-Activity context, so just return
                return;
            }
        }

        notificationManager.notify(notificationId, builder.build());
    }
}