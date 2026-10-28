package com.sante.app.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session utilisateur : token JWT + identité, gardés en mémoire et persistés dans
 * SharedPreferences privées. (Amélioration possible : EncryptedSharedPreferences / Keystore.)
 */
public final class TokenStore {

    private static final String PREFS = "sehhaty_session";
    private static final String K_TOKEN = "token";
    private static final String K_NAME = "name";
    private static final String K_EMAIL = "email";

    private static SharedPreferences prefs;
    private static volatile String token;
    private static volatile String name;
    private static volatile String email;

    private TokenStore() {}

    public static synchronized void init(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        token = prefs.getString(K_TOKEN, null);
        name = prefs.getString(K_NAME, null);
        email = prefs.getString(K_EMAIL, null);
    }

    public static synchronized void save(String newToken, String newName, String newEmail) {
        token = newToken;
        name = newName;
        email = newEmail;
        if (prefs != null) {
            prefs.edit().putString(K_TOKEN, newToken).putString(K_NAME, newName)
                    .putString(K_EMAIL, newEmail).apply();
        }
    }

    public static synchronized void clear() {
        token = null;
        name = null;
        email = null;
        if (prefs != null) prefs.edit().clear().apply();
    }

    public static boolean isLoggedIn() { return token != null && !token.isEmpty(); }
    public static String getToken() { return token; }
    public static String getName() { return name; }
    public static String getEmail() { return email; }
}
