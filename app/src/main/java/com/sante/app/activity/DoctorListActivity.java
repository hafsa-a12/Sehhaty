package com.sante.app.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.sante.app.R;
import com.sante.app.adapter.DoctorAdapter;
import com.sante.app.model.Doctor;
import com.sante.app.util.Event;
import com.sante.app.viewmodel.DoctorViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Liste des médecins : recherche, filtres par spécialité, tri par distance, réservation.
 * Toute la logique est dans {@link DoctorViewModel} ; cette Activity ne fait qu'afficher.
 */
public class DoctorListActivity extends AppCompatActivity
        implements DoctorAdapter.OnDoctorClickListener {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private DoctorAdapter adapter;
    private DoctorViewModel viewModel;
    private FusedLocationProviderClient fusedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        RecyclerView recycler = findViewById(R.id.recyclerDoctors);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAdapter(this, new ArrayList<>(), this);
        recycler.setAdapter(adapter);

        // Recherche
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { viewModel.setQuery(s.toString()); }
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }
        });

        // Filtres par spécialité ("" = tous)
        findViewById(R.id.btnTous).setOnClickListener(v -> viewModel.setSpecialite(""));
        findViewById(R.id.btnGeneraliste).setOnClickListener(v -> viewModel.setSpecialite("Generaliste"));
        findViewById(R.id.btnCardio).setOnClickListener(v -> viewModel.setSpecialite("Cardiologue"));
        findViewById(R.id.btnDentiste).setOnClickListener(v -> viewModel.setSpecialite("Dentiste"));
        findViewById(R.id.btnPediatre).setOnClickListener(v -> viewModel.setSpecialite("Pediatre"));

        // Observation du ViewModel
        viewModel.getVisibleDoctors().observe(this, doctors -> {
            if (doctors != null) adapter.updateList(doctors);
        });
        viewModel.isOffline().observe(this, offline -> {
            if (Boolean.TRUE.equals(offline)) {
                Toast.makeText(this, "Mode hors-ligne : données locales", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getMessage().observe(this, (Event<String> event) -> {
            String msg = event != null ? event.getContentIfNotHandled() : null;
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        if (savedInstanceState == null) viewModel.refresh();
        demanderLocalisation();
    }

    // ───────────── Localisation ─────────────

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void demanderLocalisation() {
        if (hasLocationPermission()) {
            obtenirPosition();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void obtenirPosition() {
        if (!hasLocationPermission()) return;
        fusedLocation.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                viewModel.setUserLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenirPosition();
        }
    }

    // ───────────── Actions sur un médecin ─────────────

    /** Choix réel de la date puis de l'heure avant d'envoyer la réservation à l'API. */
    @Override
    public void onReserverClick(Doctor doctor) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            new TimePickerDialog(this, (tv, hour, minute) -> {
                String heure = String.format(Locale.US, "%02d:%02d", hour, minute);
                viewModel.reserver(doctor, date, heure);
            }, 9, 0, true).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    @Override
    public void onCardClick(Doctor doctor) {
        Toast.makeText(this, doctor.getNom() + " - " + doctor.getAdresse(), Toast.LENGTH_SHORT).show();
    }
}
