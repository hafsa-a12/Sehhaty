package com.sante.app.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sante.app.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    /** Les plus récentes d'abord (id auto-incrémenté = ordre de création). */
    @Query("SELECT * FROM notes ORDER BY id DESC")
    LiveData<List<Note>> observeAll();

    @Query("SELECT COUNT(*) FROM notes")
    int count();

    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);
}
