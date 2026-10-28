package com.sante.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sante.app.data.HealthRepository;
import com.sante.app.model.Conseil;

import java.util.List;

public class ConseilsViewModel extends AndroidViewModel {

    private final HealthRepository repository;

    public ConseilsViewModel(@NonNull Application application) {
        super(application);
        repository = HealthRepository.getInstance(application);
    }

    public LiveData<List<Conseil>> getConseils() { return repository.getConseils(); }

    public void refresh() { repository.refreshConseils(); }
}
