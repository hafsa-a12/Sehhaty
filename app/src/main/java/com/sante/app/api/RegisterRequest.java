package com.sante.app.api;

public class RegisterRequest {
    private final String nom;
    private final String email;
    private final String password;

    public RegisterRequest(String nom, String email, String password) {
        this.nom = nom;
        this.email = email;
        this.password = password;
    }
}
