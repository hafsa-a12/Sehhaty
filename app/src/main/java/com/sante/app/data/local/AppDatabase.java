package com.sante.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sante.app.model.Doctor;
import com.sante.app.model.Note;
import com.sante.app.model.SensorData;

/** Base Room unique de l'application (singleton). */
@Database(entities = {Doctor.class, Note.class, SensorData.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Nouveau nom de fichier : évite tout conflit avec l'ancienne base SQLiteOpenHelper "sante.db"
    private static final String DB_NAME = "sehhaty_room.db";
    private static volatile AppDatabase instance;

    public abstract DoctorDao doctorDao();
    public abstract NoteDao noteDao();
    public abstract SensorDao sensorDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(), AppDatabase.class, DB_NAME).build();
                }
            }
        }
        return instance;
    }
}
