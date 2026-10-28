package com.sante.app;

import android.app.Application;

import com.sante.app.data.TokenStore;

/** Initialise la session (token JWT) dès le démarrage du processus. */
public class SehhatyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TokenStore.init(this);
    }
}
