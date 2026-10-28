package com.sante.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.sante.app.api.AppointmentRequest;
import com.sante.app.data.HealthRepository;
import com.sante.app.data.TokenStore;
import com.sante.app.model.Doctor;
import com.sante.app.util.DoctorFilter;
import com.sante.app.util.Event;

import java.util.List;

/**
 * Expose à l'UI la liste des médecins DÉJÀ filtrée / triée (recherche, spécialité, distance).
 * Survit aux rotations d'écran : plus de rechargement ni de filtre perdu.
 */
public class DoctorViewModel extends AndroidViewModel {

    private final HealthRepository repository;
    private final LiveData<List<Doctor>> allDoctors;

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MutableLiveData<String> specialite = new MutableLiveData<>("");
    private final MutableLiveData<double[]> userLocation = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> message = new MutableLiveData<>();
    private final MediatorLiveData<List<Doctor>> visibleDoctors = new MediatorLiveData<>();

    public DoctorViewModel(@NonNull Application application) {
        super(application);
        repository = HealthRepository.getInstance(application);
        allDoctors = repository.getDoctors();

        visibleDoctors.addSource(allDoctors, v -> recompute());
        visibleDoctors.addSource(query, v -> recompute());
        visibleDoctors.addSource(specialite, v -> recompute());
        visibleDoctors.addSource(userLocation, v -> recompute());
    }

    private void recompute() {
        List<Doctor> all = allDoctors.getValue();
        if (all == null) return;
        double[] loc = userLocation.getValue();
        visibleDoctors.setValue(DoctorFilter.apply(
                all, query.getValue(), specialite.getValue(),
                loc != null ? loc[0] : null, loc != null ? loc[1] : null));
    }

    // ── Sorties (observées par l'UI)
    public LiveData<List<Doctor>> getVisibleDoctors() { return visibleDoctors; }
    public LiveData<List<Doctor>> getAllDoctors() { return allDoctors; }
    public LiveData<Boolean> isOffline() { return repository.isOffline(); }
    public LiveData<Event<String>> getMessage() { return message; }

    // ── Entrées (appelées par l'UI)
    public void refresh() { repository.refreshDoctors(); }
    public void setQuery(String q) { query.setValue(q == null ? "" : q); }
    public void setSpecialite(String s) { specialite.setValue(s == null ? "" : s); }
    public void setUserLocation(double lat, double lng) { userLocation.setValue(new double[]{lat, lng}); }

    public void reserver(Doctor doctor, String date, String heure) {
        // Le backend déduit le patient du token JWT ; le nom envoyé n'est qu'indicatif.
        String patient = TokenStore.getName() != null ? TokenStore.getName() : "Patient";
        AppointmentRequest req = new AppointmentRequest(
                doctor.getId(), patient, date, heure, "Consultation");
        repository.reserver(req, error -> message.setValue(new Event<>(
                error == null
                        ? "Rendez-vous réservé chez " + doctor.getNom() + " (" + date + " " + heure + ")"
                        : error)));
    }
}
