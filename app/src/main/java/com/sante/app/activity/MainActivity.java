package com.sante.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sante.app.R;
import com.sante.app.data.TokenStore;
import com.sante.app.fragment.HomeFragment;
import com.sante.app.viewmodel.DoctorViewModel;

/** Écran principal : navigation par barre inférieure + synchronisation initiale des données. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TokenStore.isLoggedIn()) {
            goToLogin();
            return;
        }
        setContentView(R.layout.activity_main);

        TextView welcome = findViewById(R.id.textUser);
        welcome.setText(getString(R.string.bonjour_user, TokenStore.getName()));
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            TokenStore.clear();
            goToLogin();
        });

        // Initialise Room depuis le JSON puis synchronise avec l'API (une seule fois par lancement)
        if (savedInstanceState == null) {
            new ViewModelProvider(this).get(DoctorViewModel.class).refresh();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                afficherFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_doctors) {
                startActivity(new Intent(this, DoctorListActivity.class));
                return true;
            } else if (id == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            } else if (id == R.id.nav_notes) {
                startActivity(new Intent(this, NotesActivity.class));
                return true;
            } else if (id == R.id.nav_sensor) {
                startActivity(new Intent(this, SensorActivity.class));
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
            afficherFragment(new HomeFragment());
        }
    }

    /** Si le token a expiré (401 intercepté par ApiClient), on revient à la connexion. */
    @Override
    protected void onStart() {
        super.onStart();
        if (!TokenStore.isLoggedIn()) goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void afficherFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
