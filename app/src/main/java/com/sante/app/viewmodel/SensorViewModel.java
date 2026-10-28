package com.sante.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.sante.app.data.HealthRepository;
import com.sante.app.model.SensorData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Enregistre l'historique des capteurs en limitant la fréquence d'écriture par type. */
public class SensorViewModel extends AndroidViewModel {

    private static final long MIN_INTERVAL_MS = 30_000L;

    private final HealthRepository repository;
    private final java.util.Map<String, Long> lastSaved = new java.util.HashMap<>();

    public SensorViewModel(@NonNull Application application) {
        super(application);
        repository = HealthRepository.getInstance(application);
    }

    /** @return true si la mesure a été enregistrée (au plus une fois / 30 s par type). */
    public boolean saveThrottled(String type, float valeur, String unite, long nowMs) {
        Long last = lastSaved.get(type);
        if (last != null && nowMs - last < MIN_INTERVAL_MS) return false;
        lastSaved.put(type, nowMs);
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date(nowMs));
        repository.saveSensorData(new SensorData(type, valeur, unite, date));
        return true;
    }
}
