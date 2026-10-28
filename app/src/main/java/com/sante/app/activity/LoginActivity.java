package com.sante.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sante.app.R;
import com.sante.app.data.TokenStore;
import com.sante.app.viewmodel.AuthViewModel;

/** Connexion / création de compte (un seul écran, deux modes). */
public class LoginActivity extends AppCompatActivity {

    private boolean registerMode = false;

    private AuthViewModel viewModel;
    private TextInputLayout layoutNom;
    private TextInputEditText inputNom;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private Button btnSubmit;
    private TextView btnSwitch;
    private TextView title;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (TokenStore.isLoggedIn()) {
            goToMain();
            return;
        }
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        layoutNom = findViewById(R.id.layoutNom);
        inputNom = findViewById(R.id.inputNom);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSwitch = findViewById(R.id.btnSwitch);
        title = findViewById(R.id.textTitle);
        progress = findViewById(R.id.progress);

        if (savedInstanceState != null) {
            registerMode = savedInstanceState.getBoolean("registerMode", false);
        }
        renderMode();

        btnSwitch.setOnClickListener(v -> {
            registerMode = !registerMode;
            renderMode();
        });
        btnSubmit.setOnClickListener(v -> submit());

        viewModel.isLoading().observe(this, loading -> {
            boolean busy = Boolean.TRUE.equals(loading);
            progress.setVisibility(busy ? View.VISIBLE : View.GONE);
            btnSubmit.setEnabled(!busy);
        });
        viewModel.getError().observe(this, event -> {
            String msg = event == null ? null : event.getContentIfNotHandled();
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
        viewModel.getLoggedIn().observe(this, event -> {
            if (event != null && event.getContentIfNotHandled() != null) goToMain();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("registerMode", registerMode);
    }

    private void renderMode() {
        layoutNom.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        title.setText(registerMode ? R.string.creer_compte : R.string.se_connecter);
        btnSubmit.setText(registerMode ? R.string.creer_compte : R.string.se_connecter);
        btnSwitch.setText(registerMode ? R.string.deja_un_compte : R.string.pas_de_compte);
    }

    private void submit() {
        String nom = text(inputNom);
        String email = text(inputEmail);
        String password = text(inputPassword);

        if (registerMode && nom.isEmpty()) {
            inputNom.setError(getString(R.string.nom_requis));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getString(R.string.email_invalide));
            return;
        }
        if (password.length() < (registerMode ? 8 : 1)) {
            inputPassword.setError(getString(R.string.mot_de_passe_court));
            return;
        }

        if (registerMode) viewModel.register(nom, email, password);
        else viewModel.login(email, password);
    }

    private static String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
