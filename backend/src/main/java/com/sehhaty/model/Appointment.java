package com.sehhaty.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Rendez-vous. La contrainte d'unicité (médecin, date, heure) garantit en base
 * qu'un créneau ne peut pas être réservé deux fois, même en cas de requêtes concurrentes.
 */
@Entity
@Table(name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_appointment_slot",
                columnNames = {"doctor_id", "appointment_date", "appointment_time"}))
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "patient_nom", nullable = false)
    private String patientNom;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate date;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime heure;

    @Column(length = 500)
    private String motif;

    @Column(nullable = false)
    private String statut = "confirmé";

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Appointment() {}

    public Appointment(Doctor doctor, AppUser user, String patientNom,
                       LocalDate date, LocalTime heure, String motif) {
        this.doctor = doctor;
        this.user = user;
        this.patientNom = patientNom;
        this.date = date;
        this.heure = heure;
        this.motif = motif;
    }

    public Long getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public AppUser getUser() { return user; }
    public String getPatientNom() { return patientNom; }
    public LocalDate getDate() { return date; }
    public LocalTime getHeure() { return heure; }
    public String getMotif() { return motif; }
    public String getStatut() { return statut; }
    public Instant getCreatedAt() { return createdAt; }
}
