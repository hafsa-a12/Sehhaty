package com.sehhaty.dto;

import com.sehhaty.model.Appointment;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public final class AppointmentDtos {

    private AppointmentDtos() {}

    /** Même contrat JSON que l'app Android : date "AAAA-MM-JJ", heure "HH:MM". */
    public record CreateRequest(
            @NotNull(message = "doctorId requis") Long doctorId,
            @Size(max = 100) String patientNom,
            @NotNull(message = "date requise (AAAA-MM-JJ)")
            @FutureOrPresent(message = "la date ne peut pas être dans le passé") LocalDate date,
            @NotNull(message = "heure requise (HH:MM)") LocalTime heure,
            @Size(max = 500) String motif) {}

    public record Response(Long id, Long doctorId, String doctorNom, String patientNom,
                           LocalDate date, LocalTime heure, String motif, String statut) {

        public static Response from(Appointment a) {
            return new Response(a.getId(), a.getDoctor().getId(), a.getDoctor().getNom(),
                    a.getPatientNom(), a.getDate(), a.getHeure(), a.getMotif(), a.getStatut());
        }
    }
}
