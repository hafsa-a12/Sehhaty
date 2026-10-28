package com.sehhaty.service;

import com.sehhaty.dto.AuthDtos.*;
import com.sehhaty.model.AppUser;
import com.sehhaty.repository.UserRepository;
import com.sehhaty.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = normalize(req.email());
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        AppUser user = users.save(new AppUser(req.nom().trim(), email, encoder.encode(req.password())));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        AppUser user = users.findByEmail(normalize(req.email()))
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                // même message dans les deux cas : on ne révèle pas si l'email existe
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserDto me(String email) {
        AppUser u = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
        return new UserDto(u.getId(), u.getNom(), u.getEmail());
    }

    private AuthResponse toResponse(AppUser u) {
        return new AuthResponse(jwt.generateToken(u.getEmail()), "Bearer", jwt.getExpirationSeconds(),
                new UserDto(u.getId(), u.getNom(), u.getEmail()));
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
