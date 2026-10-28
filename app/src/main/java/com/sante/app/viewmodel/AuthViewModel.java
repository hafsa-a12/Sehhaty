package com.sante.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sante.app.data.AuthRepository;
import com.sante.app.util.Event;

/** État de l'écran de connexion : chargement, erreur (événement ponctuel) et succès. */
public class AuthViewModel extends ViewModel {

    private final AuthRepository repository = new AuthRepository();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> error = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> loggedIn = new MutableLiveData<>();

    public LiveData<Boolean> isLoading() { return loading; }
    public LiveData<Event<String>> getError() { return error; }
    public LiveData<Event<Boolean>> getLoggedIn() { return loggedIn; }

    public void login(String email, String password) {
        loading.setValue(true);
        repository.login(email, password, listener());
    }

    public void register(String nom, String email, String password) {
        loading.setValue(true);
        repository.register(nom, email, password, listener());
    }

    private AuthRepository.Listener listener() {
        return new AuthRepository.Listener() {
            @Override public void onSuccess() {
                loading.setValue(false);
                loggedIn.setValue(new Event<>(true));
            }
            @Override public void onError(String message) {
                loading.setValue(false);
                error.setValue(new Event<>(message));
            }
        };
    }
}
