package com.sehhaty.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "nom requis") @Size(max = 100) String nom,
            @NotBlank(message = "email requis") @Email(message = "email invalide") @Size(max = 150) String email,
            @NotBlank(message = "mot de passe requis")
            @Size(min = 8, max = 72, message = "mot de passe : 8 à 72 caractères") String password) {}

    public record LoginRequest(
            @NotBlank(message = "email requis") String email,
            @NotBlank(message = "mot de passe requis") String password) {}

    public record UserDto(Long id, String nom, String email) {}

    public record AuthResponse(String token, String type, long expiresInSeconds, UserDto user) {}
}
