package com.sante.app.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sante.app.model.Conseil;
import com.sante.app.model.Doctor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Charge les données embarquées (assets/*.json) avec Gson. */
public final class JsonHelper {

    private static final String TAG = "JsonHelper";
    private static final Gson GSON = new Gson();

    private JsonHelper() {}

    public static List<Doctor> loadDoctors(Context context) {
        return load(context, "doctors.json", new TypeToken<List<Doctor>>() {}.getType());
    }

    public static List<Conseil> loadConseils(Context context) {
        return load(context, "conseils.json", new TypeToken<List<Conseil>>() {}.getType());
    }

    private static <T> List<T> load(Context context, String assetName, Type listType) {
        try (InputStream is = context.getAssets().open(assetName);
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            List<T> list = GSON.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException | RuntimeException e) {
            Log.e(TAG, "Impossible de lire " + assetName, e);
            return new ArrayList<>();
        }
    }
}
