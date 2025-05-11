package com.example.mediassist.ui.horaire;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mediassist.R;
import com.example.mediassist.data.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HoraireActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private LinearLayout eventsContainer;
    private TextView noEventsText;
    private DatabaseHelper dbHelper;

    // Map to store events by date (using dateString as key: "yyyy-MM-dd")
    private Map<String, List<ScheduleEvent>> eventsByDate = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horaire);

        // Transparent nav bar and colored status bar
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6672FF"));
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Find views
        calendarView = findViewById(R.id.calendarView);
        eventsContainer = findViewById(R.id.eventsContainer);
        noEventsText = findViewById(R.id.noEventsText);

        // Initialize the back button
        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(v -> onBackPressed());

        // Load all events from database
        loadEvents();

        // Set up the calendar view
        setupCalendarView();

        // Show today's events by default
        showEventsForDate(Calendar.getInstance().getTime());
    }

    private void setupCalendarView() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Create a calendar instance for the selected date
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                // Show events for the selected date
                showEventsForDate(calendar.getTime());
            }
        });
    }

    private void loadEvents() {
        // Clear existing events
        eventsByDate.clear();

        // Load appointments
        loadAppointments();

        // Load medications
        loadMedications();

        // Add calendar markers
        addCalendarDateMarkers();
    }

    private void loadAppointments() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            // Query to get all appointments
            String query = "SELECT * FROM appointments";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Extract appointment data
                    int appointmentId = cursor.getInt(cursor.getColumnIndexOrThrow("appointment_id"));
                    String doctorName = cursor.getString(cursor.getColumnIndexOrThrow("doctor_name"));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                    String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("appointment_date"));
                    String timeStr = cursor.getString(cursor.getColumnIndexOrThrow("appointment_time"));

                    // Parse the date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date appointmentDate = dateFormat.parse(dateStr);

                    if (appointmentDate != null) {
                        // Create event object
                        ScheduleEvent event = new ScheduleEvent();
                        event.id = appointmentId;
                        event.title = "Consultation avec Dr. " + doctorName;
                        event.dateStr = dateStr;
                        event.timeStr = timeStr;
                        event.location = location;
                        event.type = ScheduleEvent.TYPE_APPOINTMENT;

                        // Add to map
                        addEventToMap(dateFormat.format(appointmentDate), event);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du chargement des rendez-vous", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private void loadMedications() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            // Query to get all medications with schedules
            String query = "SELECT * FROM medications";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Extract medication data
                    int medicationId = cursor.getInt(cursor.getColumnIndexOrThrow("medication_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("medication_name"));
                    String dosage = cursor.getString(cursor.getColumnIndexOrThrow("dosage"));
                    String frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency"));
                    String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
                    String endDateStr = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));

                    // Generate events for each day of medication
                    generateMedicationEvents(medicationId, name, dosage, frequency, startDateStr, endDateStr);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du chargement des médicaments", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private void generateMedicationEvents(int medicationId, String name, String dosage,
                                          String frequency, String startDateStr, String endDateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            if (startDate == null || endDate == null) return;

            // Create a calendar for iteration
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            // Determine frequency (e.g., "daily", "twice_daily", "weekly", etc.)
            String[] times = getTimesFromFrequency(frequency);

            // Generate events for each day until end date
            while (!calendar.getTime().after(endDate)) {
                Date currentDate = calendar.getTime();
                String currentDateStr = dateFormat.format(currentDate);

                // Create an event for each time on this day
                for (String time : times) {
                    ScheduleEvent event = new ScheduleEvent();
                    event.id = medicationId;
                    event.title = name;
                    event.subtitle = "Dosage: " + dosage;
                    event.dateStr = currentDateStr;
                    event.timeStr = time;
                    event.type = ScheduleEvent.TYPE_MEDICATION;

                    // Add to map
                    addEventToMap(currentDateStr, event);
                }

                // Move to next day
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getTimesFromFrequency(String frequency) {
        // Default is once daily at morning
        if (frequency == null) return new String[] {"08:00"};

        switch (frequency.toLowerCase()) {
            case "once_daily_morning":
                return new String[] {"08:00"};
            case "once_daily_evening":
                return new String[] {"20:00"};
            case "twice_daily":
                return new String[] {"08:00", "20:00"};
            case "three_times_daily":
                return new String[] {"08:00", "14:00", "20:00"};
            case "weekly":
                return new String[] {"10:00"};  // Once per week
            case "as_needed":
                return new String[] {"12:00"};  // Just a placeholder
            default:
                return new String[] {"08:00"};  // Default
        }
    }

    private void addEventToMap(String dateStr, ScheduleEvent event) {
        if (!eventsByDate.containsKey(dateStr)) {
            eventsByDate.put(dateStr, new ArrayList<>());
        }
        eventsByDate.get(dateStr).add(event);
    }

    private void showEventsForDate(Date date) {
        // Clear current events
        eventsContainer.removeAllViews();

        // Format the date to match our map keys
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = dateFormat.format(date);

        // Get events for the selected date
        List<ScheduleEvent> events = eventsByDate.get(dateStr);

        if (events != null && !events.isEmpty()) {
            // Sort events by time
            events.sort((e1, e2) -> e1.timeStr.compareTo(e2.timeStr));

            // Show each event
            for (ScheduleEvent event : events) {
                addEventCard(event);
            }

            // Hide "no events" message
            noEventsText.setVisibility(View.GONE);
        } else {
            // Show "no events" message
            noEventsText.setVisibility(View.VISIBLE);
        }
    }

    private void addEventCard(ScheduleEvent event) {
        CardView cardView;
        if (event.type == ScheduleEvent.TYPE_APPOINTMENT) {
            cardView = createAppointmentCard(event);
        } else {
            cardView = createMedicationCard(event);
        }

        eventsContainer.addView(cardView);
    }

    private CardView createAppointmentCard(ScheduleEvent event) {
        View cardView = getLayoutInflater().inflate(R.layout.card_appointment, null);

        // Find views
        TextView titleText = cardView.findViewById(R.id.appointmentTitle);
        TextView dateTimeText = cardView.findViewById(R.id.appointmentDateTime);
        TextView locationText = cardView.findViewById(R.id.appointmentLocation);

        // Set data
        titleText.setText(event.title);
        dateTimeText.setText(formatDateTime(event.dateStr, event.timeStr));
        locationText.setText(event.location);

        return (CardView) cardView;
    }

    private CardView createMedicationCard(ScheduleEvent event) {
        View cardView = getLayoutInflater().inflate(R.layout.card_medication, null);

        // Find views
        TextView titleText = cardView.findViewById(R.id.medicationTitle);
        TextView subtitleText = cardView.findViewById(R.id.medicationSubtitle);
        TextView timeText = cardView.findViewById(R.id.medicationTime);

        // Set data
        titleText.setText(event.title);
        subtitleText.setText(event.subtitle);
        timeText.setText(event.timeStr);

        return (CardView) cardView;
    }

    private String formatDateTime(String dateStr, String timeStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = dateFormat.parse(dateStr);

            if (date != null) {
                // Format to display date in a user-friendly format
                SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE d MMMM", Locale.FRENCH);
                return displayFormat.format(date) + ", " + timeStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateStr + ", " + timeStr;
    }

    // Class to represent events on the schedule
    private static class ScheduleEvent {
        public static final int TYPE_APPOINTMENT = 1;
        public static final int TYPE_MEDICATION = 2;

        public int id;
        public String title;
        public String subtitle;
        public String dateStr;
        public String timeStr;
        public String location;
        public int type;
    }

    private void addCalendarDateMarkers() {
        // Create a string of dates with events
        StringBuilder markerText = new StringBuilder("Dates avec évènements: ");
        boolean first = true;

        // Sort dates
        List<String> sortedDates = new ArrayList<>(eventsByDate.keySet());
        Collections.sort(sortedDates);

        // Format dates for display
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.FRENCH);

        for (String dateStr : sortedDates) {
            try {
                Date date = inputFormat.parse(dateStr);
                if (date != null) {
                    if (!first) {
                        markerText.append(", ");
                    }
                    markerText.append(outputFormat.format(date));
                    first = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set the text
        TextView markersText = findViewById(R.id.calendarMarkers);
        if (markersText != null) {
            if (first) {
                // No dates with events
                markersText.setVisibility(View.GONE);
            } else {
                markersText.setText(markerText.toString());
                markersText.setVisibility(View.VISIBLE);
            }
        }
    }
}