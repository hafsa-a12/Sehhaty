package com.sehhaty.service;

import com.sehhaty.dto.DoctorDto;
import com.sehhaty.model.Doctor;
import com.sehhaty.repository.DoctorRepository;
import com.sehhaty.util.GeoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctors;

    public DoctorService(DoctorRepository doctors) {
        this.doctors = doctors;
    }

    public List<DoctorDto> findAll() {
        return doctors.findAll().stream().map(DoctorDto::from).toList();
    }

    /** Médecins dans un rayon (km) autour d'un point, triés du plus proche au plus éloigné. */
    public List<DoctorDto> findNearby(double lat, double lng, double radiusKm) {
        return doctors.findAll().stream()
                .map(d -> DoctorDto.from(d, round1(distanceTo(d, lat, lng))))
                .filter(dto -> dto.distance() <= radiusKm)
                .sorted(Comparator.comparingDouble(DoctorDto::distance))
                .toList();
    }

    private static double distanceTo(Doctor d, double lat, double lng) {
        return GeoUtils.haversineKm(lat, lng, d.getLatitude(), d.getLongitude());
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
