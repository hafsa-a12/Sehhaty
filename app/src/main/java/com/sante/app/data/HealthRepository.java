package com.sante.app.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sante.app.api.ApiClient;
import com.sante.app.api.ApiErrors;
import com.sante.app.api.ApiService;
import com.sante.app.api.AppointmentRequest;
import com.sante.app.data.local.AppDatabase;
import com.sante.app.data.local.DoctorDao;
import com.sante.app.data.local.NoteDao;
import com.sante.app.data.local.SensorDao;
import com.sante.app.helper.JsonHelper;
import com.sante.app.model.Conseil;
import com.sante.app.model.Doctor;
import com.sante.app.model.Note;
import com.sante.app.model.SensorData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Point d'entrée unique des données (pattern Repository).
 *
 * Stratégie "offline-first" :
 *  1. Room est la source de vérité : l'UI observe toujours la base (LiveData).
 *  2. Au 1er lancement, la base est initialisée depuis assets/*.json.
 *  3. On tente ensuite l'API ; en cas de succès on met à jour Room, sinon on reste hors-ligne.
 *
 * Toutes les opérations disque / réseau tournent sur un thread d'arrière-plan.
 */
public class HealthRepository {

    private static final String TAG = "HealthRepository";
    private static volatile HealthRepository instance;

    private final Context appContext;
    private final DoctorDao doctorDao;
    private final NoteDao noteDao;
    private final SensorDao sensorDao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Boolean> offline = new MutableLiveData<>(false);
    private final MutableLiveData<List<Conseil>> conseils = new MutableLiveData<>();

    private HealthRepository(Context context) {
        this.appContext = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(appContext);
        this.doctorDao = db.doctorDao();
        this.noteDao = db.noteDao();
        this.sensorDao = db.sensorDao();
    }

    public static HealthRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (HealthRepository.class) {
                if (instance == null) instance = new HealthRepository(context);
            }
        }
        return instance;
    }

    // ───────────────────────── Médecins ─────────────────────────

    public LiveData<List<Doctor>> getDoctors() {
        return doctorDao.observeAll();
    }

    /** true quand la dernière tentative réseau a échoué (mode hors-ligne). */
    public LiveData<Boolean> isOffline() {
        return offline;
    }

    /** Initialise depuis le JSON si la base est vide, puis synchronise avec l'API. */
    public void refreshDoctors() {
        io.execute(() -> {
            if (doctorDao.count() == 0) {
                List<Doctor> seed = JsonHelper.loadDoctors(appContext);
                if (!seed.isEmpty()) doctorDao.replaceAll(seed);
            }
            ApiService api = ApiClient.getService();
            try {
                Response<List<Doctor>> r = api.getAllDoctors().execute();
                List<Doctor> body = r.body();
                if (r.isSuccessful() && body != null && !body.isEmpty()) {
                    doctorDao.replaceAll(body);
                    offline.postValue(false);
                } else {
                    Log.w(TAG, "API médecins : code " + r.code());
                    offline.postValue(true);
                }
            } catch (IOException e) {
                Log.w(TAG, "API médecins injoignable : " + e.getMessage());
                offline.postValue(true);
            }
        });
    }

    /** Résultat d'une réservation : null = succès, sinon le message d'erreur à afficher. */
    public interface ReservationCallback {
        void onResult(String errorMessage);
    }

    public void reserver(AppointmentRequest request, ReservationCallback callback) {
        ApiClient.getService().reserverRdv(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onResult(null);
                } else if (response.code() == 401) {
                    callback.onResult("Session expirée : reconnecte-toi");
                } else {
                    // 409 créneau pris / médecin indisponible, 422 date passée...
                    callback.onResult(ApiErrors.fromBody(response.errorBody(),
                            "Réservation impossible (code " + response.code() + ")"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onResult("Réservation impossible : serveur injoignable");
            }
        });
    }

    // ───────────────────────── Conseils ─────────────────────────

    public LiveData<List<Conseil>> getConseils() {
        return conseils;
    }

    /** API en priorité, sinon JSON embarqué. */
    public void refreshConseils() {
        io.execute(() -> {
            try {
                Response<List<Conseil>> r = ApiClient.getService().getConseils().execute();
                List<Conseil> body = r.body();
                if (r.isSuccessful() && body != null && !body.isEmpty()) {
                    conseils.postValue(body);
                    return;
                }
            } catch (IOException e) {
                Log.w(TAG, "API conseils injoignable : " + e.getMessage());
            }
            conseils.postValue(JsonHelper.loadConseils(appContext));
        });
    }

    // ───────────────────────── Notes ─────────────────────────

    public LiveData<List<Note>> getNotes() {
        return noteDao.observeAll();
    }

    public void insertNote(Note note) {
        io.execute(() -> noteDao.insert(note));
    }

    public void updateNote(Note note) {
        io.execute(() -> noteDao.update(note));
    }

    public void deleteNote(Note note) {
        io.execute(() -> noteDao.delete(note));
    }

    // ───────────────────────── Capteurs ─────────────────────────

    public void saveSensorData(SensorData data) {
        io.execute(() -> sensorDao.insert(data));
    }

    public LiveData<List<SensorData>> getSensorHistory(String type) {
        return sensorDao.history(type);
    }
}
