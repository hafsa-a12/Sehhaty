package com.sehhaty.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sehhaty.model.Doctor;

/** "distance" n'est renseignée que pour /doctors/nearby. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DoctorDto(Long id, String nom, String specialite, String telephone, String adresse,
                        double latitude, double longitude, boolean disponible, double note,
                        Double distance) {

    public static DoctorDto from(Doctor d) {
        return from(d, null);
    }

    public static DoctorDto from(Doctor d, Double distanceKm) {
        return new DoctorDto(d.getId(), d.getNom(), d.getSpecialite(), d.getTelephone(),
                d.getAdresse(), d.getLatitude(), d.getLongitude(), d.isDisponible(),
                d.getNote(), distanceKm);
    }
}
