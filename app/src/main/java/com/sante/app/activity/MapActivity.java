package com.sante.app.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sante.app.R;
import com.sante.app.model.Doctor;
import com.sante.app.viewmodel.DoctorViewModel;

import java.util.List;

/** Carte Google Maps : médecins en vert (disponible) ou orange (occupé), centrée sur l'utilisateur. */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 200;
    private static final LatLng RABAT = new LatLng(34.0132, -6.8325);

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocation;
    private List<Doctor> doctors; // dernière liste reçue du ViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        // La carte et les données arrivent dans un ordre indéterminé : on dessine dès que les deux sont là.
        DoctorViewModel viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        viewModel.getAllDoctors().observe(this, list -> {
            doctors = list;
            placerMarqueurs();
        });
        if (savedInstanceState == null) viewModel.refresh();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Erreur : fragment carte introuvable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(marker -> {
            Toast.makeText(this, marker.getTitle() + "\n" + marker.getSnippet(), Toast.LENGTH_LONG).show();
            return false;
        });

        if (hasLocationPermission()) {
            activerPosition();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(RABAT, 13));
        }
        placerMarqueurs();
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void activerPosition() {
        if (!hasLocationPermission() || map == null) return;
        map.setMyLocationEnabled(true);
        fusedLocation.getLastLocation().addOnSuccessListener(location -> {
            LatLng target = location != null
                    ? new LatLng(location.getLatitude(), location.getLongitude())
                    : RABAT;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, location != null ? 14 : 13));
        });
    }

    private void placerMarqueurs() {
        if (map == null || doctors == null) return;
        map.clear();
        for (Doctor d : doctors) {
            float couleur = d.isDisponible()
                    ? BitmapDescriptorFactory.HUE_GREEN
                    : BitmapDescriptorFactory.HUE_ORANGE;
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(d.getLatitude(), d.getLongitude()))
                    .title(d.getNom())
                    .snippet(d.getSpecialite() + " - " + d.getTelephone())
                    .icon(BitmapDescriptorFactory.defaultMarker(couleur)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            activerPosition();
        } else {
            Toast.makeText(this, "Permission refusée : carte centrée sur Rabat", Toast.LENGTH_SHORT).show();
        }
    }
}
