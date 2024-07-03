package com.example.alarames;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int hour;
    public int minute;
    public String days;
    public boolean isEnabled;  // Yeni alan

    public Alarm(int hour, int minute, String days) {
        this.hour = hour;
        this.minute = minute;
        this.days = days;
        this.isEnabled = false;  // Varsayılan olarak kapalı
    }
}
