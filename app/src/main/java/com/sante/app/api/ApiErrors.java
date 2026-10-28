package com.sante.app.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.ResponseBody;

/** Extrait le message d'erreur du backend : {"error": "..."} ou {"errors": ["...", ...]}. */
public final class ApiErrors {

    private ApiErrors() {}

    public static String fromBody(ResponseBody body, String fallback) {
        if (body == null) return fallback;
        try {
            return fromJson(body.string(), fallback);
        } catch (Exception e) {
            return fallback;
        }
    }

    /** Séparé de fromBody : sans dépendance Android. */
    public static String fromJson(String json, String fallback) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("error")) return obj.get("error").getAsString();
            if (obj.has("errors")) {
                JsonArray arr = obj.getAsJsonArray("errors");
                StringBuilder sb = new StringBuilder();
                for (JsonElement e : arr) {
                    if (sb.length() > 0) sb.append('\n');
                    sb.append(e.getAsString());
                }
                if (sb.length() > 0) return sb.toString();
            }
        } catch (Exception ignored) {
            // corps non JSON : on retombe sur le message par défaut
        }
        return fallback;
    }
}
