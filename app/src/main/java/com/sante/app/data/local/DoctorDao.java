package com.sante.app.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sante.app.model.Doctor;

import java.util.List;

@Dao
public abstract class DoctorDao {

    @Query("SELECT * FROM doctors ORDER BY nom")
    public abstract LiveData<List<Doctor>> observeAll();

    @Query("SELECT COUNT(*) FROM doctors")
    public abstract int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<Doctor> doctors);

    @Query("DELETE FROM doctors")
    public abstract void deleteAll();

    /** Remplace tout le contenu en une seule transaction (atomique). */
    @Transaction
    public void replaceAll(List<Doctor> doctors) {
        deleteAll();
        insertAll(doctors);
    }
}
