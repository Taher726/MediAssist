package com.example.mediassist.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.models.NotificationModel;
import com.example.mediassist.services.NotificationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int type = intent.getIntExtra("type", 0);
        int relatedId = 0;

        if (type == NotificationModel.TYPE_MEDICATION) {
            relatedId = intent.getIntExtra("medicationId", 0);
        } else if (type == NotificationModel.TYPE_APPOINTMENT) {
            relatedId = intent.getIntExtra("appointmentId", 0);
        }

        // Show notification
        NotificationService notificationService = new NotificationService(context);
        notificationService.showNotification(title, message, type * 10000 + relatedId);

        // Save notification to database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        NotificationModel notification = new NotificationModel();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setDateTime(currentDateTime);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setRead(false);

        dbHelper.addNotification(notification);
    }
}