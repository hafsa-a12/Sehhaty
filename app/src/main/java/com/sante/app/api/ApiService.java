package com.sante.app.api;

import com.sante.app.model.Conseil;
import com.sante.app.model.Doctor;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/** Contrat de l'API REST (voir /server pour une implémentation de développement). */
public interface ApiService {

    @GET("doctors")
    Call<List<Doctor>> getAllDoctors();

    @GET("conseils")
    Call<List<Conseil>> getConseils();

    // ── Authentification (JWT) ──
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    // ── Réservation : exige un token (ajouté automatiquement par ApiClient) ──
    @POST("appointments")
    Call<ResponseBody> reserverRdv(@Body AppointmentRequest request);
}
