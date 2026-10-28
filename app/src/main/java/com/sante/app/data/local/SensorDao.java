package com.sante.app.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.sante.app.model.SensorData;

import java.util.List;

@Dao
public interface SensorDao {

    @Insert
    void insert(SensorData data);

    /** 50 dernières mesures d'un type de capteur. */
    @Query("SELECT * FROM capteurs WHERE type = :type ORDER BY id DESC LIMIT 50")
    LiveData<List<SensorData>> history(String type);

    @Query("SELECT COUNT(*) FROM capteurs")
    int count();
}
