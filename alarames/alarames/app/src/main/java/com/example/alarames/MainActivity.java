package com.example.alarames;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.OnAlarmListener {
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    AppDatabase db;
    AlarmDao alarmDao;
    RecyclerView recyclerView;
    AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmTimePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        recyclerView = findViewById(R.id.recyclerView);

        db = AppDatabase.getDatabase(getApplicationContext());
        alarmDao = db.alarmDao();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displayAlarms();
    }

    public void OnToggleClicked(View view) {
        long time;
        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(MainActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                time = time + (1000 * 60 * 60 * 24);
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);

            String days = getSelectedDays();
            Alarm alarm = new Alarm(alarmTimePicker.getHour(), alarmTimePicker.getMinute(), days);
            alarmDao.insert(alarm);
            displayAlarms();

        } else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(MainActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedDays() {
        CheckBox monday = findViewById(R.id.checkBoxMonday);
        CheckBox tuesday = findViewById(R.id.checkBoxTuesday);
        CheckBox wednesday = findViewById(R.id.checkBoxWednesday);
        CheckBox thursday = findViewById(R.id.checkBoxThursday);
        CheckBox friday = findViewById(R.id.checkBoxFriday);
        CheckBox saturday = findViewById(R.id.checkBoxSaturday);
        CheckBox sunday = findViewById(R.id.checkBoxSunday);

        StringBuilder days = new StringBuilder();

        if (monday.isChecked()) days.append("Mon ");
        if (tuesday.isChecked()) days.append("Tue ");
        if (wednesday.isChecked()) days.append("Wed ");
        if (thursday.isChecked()) days.append("Thu ");
        if (friday.isChecked()) days.append("Fri ");
        if (saturday.isChecked()) days.append("Sat ");
        if (sunday.isChecked()) days.append("Sun ");

        return days.toString().trim();
    }

    private void displayAlarms() {
        List<Alarm> alarms = alarmDao.getAllAlarms();
        alarmAdapter = new AlarmAdapter(this, alarms, this);
        recyclerView.setAdapter(alarmAdapter);
    }

    @Override
    public void onAlarmToggle(Alarm alarm) {
        if (alarm.isEnabled) {
            cancelAlarm(alarm);
            Toast.makeText(this, "Alarm Off: " + alarm.id, Toast.LENGTH_SHORT).show();
        } else {
            setAlarm(alarm);
            Toast.makeText(this, "Alarm On: " + alarm.id, Toast.LENGTH_SHORT).show();
        }
        alarm.isEnabled = !alarm.isEnabled;  // Durumu değiştir
        alarmDao.update(alarm);  // Durumu veritabanında güncelle
        displayAlarms();  // RecyclerView'u güncelle
    }

    private void setAlarm(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hour);
        calendar.set(Calendar.MINUTE, alarm.minute);

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_IMMUTABLE);

        long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time) {
            time = time + (1000 * 60 * 60 * 24);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelAlarm(Alarm alarm) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
    @Override
    public void onAlarmDelete(Alarm alarm) {
        alarmDao.delete(alarm);
        displayAlarms();
        Toast.makeText(this, "Deleted alarm: " + alarm.id, Toast.LENGTH_SHORT).show();
    }
}
