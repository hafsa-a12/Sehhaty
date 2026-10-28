package com.sante.app.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.sante.app.R;
import com.sante.app.util.FallDetector;
import com.sante.app.util.StepProgress;
import com.sante.app.util.StepTracker;
import com.sante.app.viewmodel.SensorViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Capteurs de santé : podomètre (pas du jour + objectif), accéléromètre (détection de chute),
 * lumière ambiante. Les calculs sont dans des classes pures (StepTracker, FallDetector,
 * StepProgress) ; l'historique est enregistré via SensorViewModel (au plus 1 écriture / 30 s / type).
 */
public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private static final int ACTIVITY_RECOGNITION_CODE = 101;
    private static final int OBJECTIF_PAS = 8000;
    private static final String PREFS = "steps_prefs";
    private static final String KEY_BASELINE = "baseline";
    private static final String KEY_BASELINE_DATE = "baseline_date";

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private Sensor accelerometerSensor;
    private Sensor lightSensor;

    private TextView tvPas, tvObjectif, tvAccelX, tvAccelY, tvAccelZ, tvLumiere, tvStatutPas;
    private ProgressBar progressPas;

    private SensorViewModel viewModel;
    private SharedPreferences prefs;
    private StepTracker stepTracker;
    private final FallDetector fallDetector = new FallDetector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        viewModel = new ViewModelProvider(this).get(SensorViewModel.class);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        stepTracker = new StepTracker(prefs.getInt(KEY_BASELINE, 0), prefs.getString(KEY_BASELINE_DATE, null));

        tvPas = findViewById(R.id.tvPas);
        tvObjectif = findViewById(R.id.tvObjectif);
        tvAccelX = findViewById(R.id.tvAccelX);
        tvAccelY = findViewById(R.id.tvAccelY);
        tvAccelZ = findViewById(R.id.tvAccelZ);
        tvLumiere = findViewById(R.id.tvLumiere);
        tvStatutPas = findViewById(R.id.tvStatutPas);
        progressPas = findViewById(R.id.progressPas);

        progressPas.setMax(OBJECTIF_PAS);
        tvObjectif.setText("Objectif : " + OBJECTIF_PAS + " pas");

        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (stepCounterSensor == null) {
            tvStatutPas.setText("Podomètre non disponible sur cet appareil");
        }
        demanderPermissionPodometre();
    }

    // ───────────── Permission ACTIVITY_RECOGNITION (obligatoire dès Android 10 pour le podomètre) ─────────────

    private boolean podometreAutorise() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void demanderPermissionPodometre() {
        if (!podometreAutorise()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, ACTIVITY_RECOGNITION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_RECOGNITION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enregistrerPodometre();
            } else {
                tvStatutPas.setText("Autorise la reconnaissance d'activité pour compter tes pas");
            }
        }
    }

    // ───────────── Cycle de vie : on libère les capteurs quand l'écran n'est plus visible ─────────────

    @Override
    protected void onResume() {
        super.onResume();
        if (podometreAutorise()) enregistrerPodometre();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void enregistrerPodometre() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // ───────────── Événements capteurs ─────────────

    @Override
    public void onSensorChanged(SensorEvent event) {
        long now = System.currentTimeMillis();
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER:
                onSteps((int) event.values[0], now);
                break;

            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0], y = event.values[1], z = event.values[2];
                tvAccelX.setText(String.format(Locale.getDefault(), "X : %.2f m/s²", x));
                tvAccelY.setText(String.format(Locale.getDefault(), "Y : %.2f m/s²", y));
                tvAccelZ.setText(String.format(Locale.getDefault(), "Z : %.2f m/s²", z));
                if (fallDetector.onSample(x, y, z, now)) {
                    Toast.makeText(this, "Chute possible détectée !", Toast.LENGTH_LONG).show();
                }
                break;

            case Sensor.TYPE_LIGHT:
                float lux = event.values[0];
                tvLumiere.setText(String.format(Locale.getDefault(), "%.0f lux", lux));
                viewModel.saveThrottled("lumiere", lux, "lux", now);
                break;

            default:
                break;
        }
    }

    private void onSteps(int totalSinceBoot, long now) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(now));
        int pasAujourdHui = stepTracker.onTotal(totalSinceBoot, today);

        prefs.edit()
                .putInt(KEY_BASELINE, stepTracker.getBaseline())
                .putString(KEY_BASELINE_DATE, stepTracker.getBaselineDate())
                .apply();

        tvPas.setText(String.valueOf(pasAujourdHui));
        progressPas.setProgress(Math.min(pasAujourdHui, OBJECTIF_PAS));

        if (StepProgress.isGoalReached(pasAujourdHui, OBJECTIF_PAS)) {
            tvStatutPas.setText("Objectif atteint !");
        } else {
            tvStatutPas.setText(StepProgress.percent(pasAujourdHui, OBJECTIF_PAS) + "% - encore "
                    + StepProgress.remaining(pasAujourdHui, OBJECTIF_PAS) + " pas");
        }
        viewModel.saveThrottled("pas", pasAujourdHui, "pas", now);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
