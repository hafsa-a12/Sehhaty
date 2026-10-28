package com.sehhaty.repository;

import com.sehhaty.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndDateAndHeure(Long doctorId, LocalDate date, LocalTime heure);

    /** JOIN FETCH : charge le médecin en une requête (évite le problème N+1). */
    @Query("select a from Appointment a join fetch a.doctor "
            + "where a.user.id = :userId order by a.date, a.heure")
    List<Appointment> findByUserId(@Param("userId") Long userId);

    @Query("select a from Appointment a join fetch a.doctor "
            + "where a.id = :id and a.user.id = :userId")
    Optional<Appointment> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
