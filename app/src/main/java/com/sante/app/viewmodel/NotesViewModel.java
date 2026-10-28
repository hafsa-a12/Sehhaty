package com.sante.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sante.app.data.HealthRepository;
import com.sante.app.model.Note;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private final HealthRepository repository;
    private final LiveData<List<Note>> notes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        repository = HealthRepository.getInstance(application);
        notes = repository.getNotes();
    }

    public LiveData<List<Note>> getNotes() { return notes; }

    public void add(Note note) { repository.insertNote(note); }
    public void update(Note note) { repository.updateNote(note); }
    public void delete(Note note) { repository.deleteNote(note); }
}
