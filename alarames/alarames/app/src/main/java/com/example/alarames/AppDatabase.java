package com.example.alarames;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

@Database(entities = {Alarm.class}, version = 2)  // Versiyon numarasını güncelledik
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "alarm_database")
                            .addMigrations(MIGRATION_1_2)  // Migration ekledik
                            .allowMainThreadQueries()  // Not recommended for production
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Yeni sütun eklemek için SQL komutunu burada çalıştırın
            database.execSQL("ALTER TABLE alarms ADD COLUMN isEnabled INTEGER NOT NULL DEFAULT 0");
        }
    };
}
