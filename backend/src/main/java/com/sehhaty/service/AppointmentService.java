package com.sehhaty.service;

import com.sehhaty.dto.AppointmentDtos.CreateRequest;
import com.sehhaty.dto.AppointmentDtos.Response;
import com.sehhaty.model.AppUser;
import com.sehhaty.model.Appointment;
import com.sehhaty.model.Doctor;
import com.sehhaty.repository.AppointmentRepository;
import com.sehhaty.repository.DoctorRepository;
import com.sehhaty.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointments;
    private final DoctorRepository doctors;
    private final UserRepository users;

    public AppointmentService(AppointmentRepository appointments, DoctorRepository doctors,
                              UserRepository users) {
        this.appointments = appointments;
        this.doctors = doctors;
        this.users = users;
    }

    @Transactional
    public Response create(String userEmail, CreateRequest req) {
        AppUser user = users.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
        Doctor doctor = doctors.findById(req.doctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médecin introuvable"));

        if (!doctor.isDisponible()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce médecin n'est pas disponible");
        }
        if (LocalDateTime.of(req.date(), req.heure()).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Le créneau est déjà passé");
        }
        // Vérification rapide ; la contrainte UNIQUE en base reste le vrai garde-fou (concurrence).
        if (appointments.existsByDoctorIdAndDateAndHeure(doctor.getId(), req.date(), req.heure())) {
            throw slotTaken();
        }

        String patient = (req.patientNom() == null || req.patientNom().isBlank())
                ? user.getNom() : req.patientNom().trim();
        try {
            Appointment saved = appointments.saveAndFlush(
                    new Appointment(doctor, user, patient, req.date(), req.heure(), req.motif()));
            return Response.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw slotTaken();
        }
    }

    @Transactional(readOnly = true)
    public List<Response> listMine(String userEmail) {
        AppUser user = users.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
        return appointments.findByUserId(user.getId()).stream().map(Response::from).toList();
    }

    /** Un utilisateur ne peut annuler que ses propres rendez-vous (sinon 404, on ne révèle rien). */
    @Transactional
    public void cancel(String userEmail, Long id) {
        AppUser user = users.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
        Appointment a = appointments.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable"));
        appointments.delete(a);
    }

    private static ResponseStatusException slotTaken() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Ce créneau est déjà réservé");
    }
}
