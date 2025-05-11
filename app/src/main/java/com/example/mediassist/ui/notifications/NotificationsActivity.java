package com.example.mediassist.ui.notifications;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;
import com.example.mediassist.data.models.NotificationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private TextView emptyStateMessage;
    private TextView clearAllButton;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationsList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Transparent nav bar and colored status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Initialize views
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        emptyStateMessage = findViewById(R.id.emptyStateMessage);
        clearAllButton = findViewById(R.id.clearAllButton);
        ImageView backIcon = findViewById(R.id.backIcon);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Set up back button
        backIcon.setOnClickListener(v -> finish());

        // Set up clear all button
        clearAllButton.setOnClickListener(v -> {
            clearAllNotifications();
        });

        // Set up RecyclerView
        notificationsList = new ArrayList<>();
        adapter = new NotificationsAdapter(notificationsList);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setAdapter(adapter);

        // Load notifications
        loadNotifications();
    }

    private void loadNotifications() {
        notificationsList.clear();
        notificationsList.addAll(dbHelper.getAllNotifications());

        adapter.notifyDataSetChanged();

        // Update empty state
        if (notificationsList.isEmpty()) {
            emptyStateMessage.setVisibility(View.VISIBLE);
        } else {
            emptyStateMessage.setVisibility(View.GONE);
        }
    }

    private void clearAllNotifications() {
        // Implement clear all functionality
        for (NotificationModel notification : notificationsList) {
            dbHelper.deleteNotification(notification.getId());
        }

        notificationsList.clear();
        adapter.notifyDataSetChanged();
        emptyStateMessage.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Toutes les notifications ont été supprimées", Toast.LENGTH_SHORT).show();
    }

    // Adapter for notifications RecyclerView
    private class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

        private List<NotificationModel> notifications;

        public NotificationsAdapter(List<NotificationModel> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificationModel notification = notifications.get(position);

            holder.titleTextView.setText(notification.getTitle());
            holder.messageTextView.setText(notification.getMessage());

            // Format time relative to now
            holder.timeTextView.setText(getTimeAgo(notification.getDateTime()));

            // Set icon based on notification type
            if (notification.getType() == NotificationModel.TYPE_MEDICATION) {
                holder.typeIconView.setImageResource(R.drawable.stethoscope);
            } else {
                holder.typeIconView.setImageResource(R.drawable.appointment);
            }

            // Show/hide unread indicator
            holder.unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

            // Set background color based on read status
            holder.itemView.setBackgroundColor(notification.isRead()
                    ? Color.WHITE : Color.parseColor("#F0F7FF"));

            // Set click listener for the item
            holder.itemView.setOnClickListener(v -> {
                // Mark as read
                if (!notification.isRead()) {
                    dbHelper.markNotificationAsRead(notification.getId());
                    notification.setRead(true);
                    holder.unreadIndicator.setVisibility(View.GONE);
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }
            });

            // Set click listener for delete button
            holder.deleteButton.setOnClickListener(v -> {
                dbHelper.deleteNotification(notification.getId());
                int pos = notifications.indexOf(notification);
                if (pos != -1) {
                    notifications.remove(pos);
                    notifyItemRemoved(pos);

                    // Update empty state
                    if (notifications.isEmpty()) {
                        emptyStateMessage.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView messageTextView;
            TextView timeTextView;
            ImageView typeIconView;
            View unreadIndicator;
            ImageView deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.notificationTitle);
                messageTextView = itemView.findViewById(R.id.notificationMessage);
                timeTextView = itemView.findViewById(R.id.notificationTime);
                typeIconView = itemView.findViewById(R.id.notificationTypeIcon);
                unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
                deleteButton = itemView.findViewById(R.id.deleteNotification);
            }
        }

        // Helper method to format time ago
        private String getTimeAgo(String dateTimeStr) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date past = sdf.parse(dateTimeStr);
                Date now = new Date();
                long seconds = (now.getTime() - past.getTime()) / 1000;

                if (seconds < 60) {
                    return "À l'instant";
                } else if (seconds < 60 * 60) {
                    return "Il y a " + (seconds / 60) + " minutes";
                } else if (seconds < 60 * 60 * 24) {
                    return "Il y a " + (seconds / (60 * 60)) + " heures";
                } else {
                    return "Il y a " + (seconds / (60 * 60 * 24)) + " jours";
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return dateTimeStr;
            }
        }
    }
}