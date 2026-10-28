package com.sante.app.data;

import com.sante.app.api.ApiClient;
import com.sante.app.api.ApiErrors;
import com.sante.app.api.AuthResponse;
import com.sante.app.api.LoginRequest;
import com.sante.app.api.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Connexion / inscription : en cas de succès, le token est sauvegardé dans TokenStore. */
public class AuthRepository {

    public interface Listener {
        void onSuccess();
        void onError(String message);
    }

    public void login(String email, String password, Listener listener) {
        ApiClient.getService().login(new LoginRequest(email, password)).enqueue(handler(listener));
    }

    public void register(String nom, String email, String password, Listener listener) {
        ApiClient.getService().register(new RegisterRequest(nom, email, password)).enqueue(handler(listener));
    }

    public void logout() {
        TokenStore.clear();
    }

    private Callback<AuthResponse> handler(Listener listener) {
        return new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                AuthResponse body = response.body();
                if (response.isSuccessful() && body != null && body.getToken() != null && body.getUser() != null) {
                    TokenStore.save(body.getToken(), body.getUser().getNom(), body.getUser().getEmail());
                    listener.onSuccess();
                } else {
                    listener.onError(ApiErrors.fromBody(response.errorBody(),
                            "Échec de l'authentification (code " + response.code() + ")"));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                listener.onError("Serveur injoignable. Vérifie que l'API tourne et l'URL dans local.properties.");
            }
        };
    }
}
