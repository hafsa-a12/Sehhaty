package com.sante.app.api;

/** Réponse de /auth/login et /auth/register : {token, type, expiresInSeconds, user{id,nom,email}}. */
public class AuthResponse {
    private String token;
    private User user;

    public String getToken() { return token; }
    public User getUser() { return user; }

    public static class User {
        private long id;
        private String nom;
        private String email;

        public long getId() { return id; }
        public String getNom() { return nom; }
        public String getEmail() { return email; }
    }
}
