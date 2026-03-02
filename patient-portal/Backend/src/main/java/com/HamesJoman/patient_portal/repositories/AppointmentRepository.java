package com.HamesJoman.patient_portal.repositories;

import com.HamesJoman.patient_portal.models.Appointment;
import com.HamesJoman.patient_portal.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for Appointment entities.
 * Provides standard CRUD operations via JpaRepository, plus custom queries
 * for conflict detection and automatic status expiration.
 *
 * Conflict checks only look at ACTIVE appointments — CANCELLED and FINISHED
 * appointments do not block new bookings in the same time slot.
 *
 * @author Nathan Amidon
 *
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // -------------------------------------------------------------------------
    // Basic lookups
    // -------------------------------------------------------------------------

    /** All appointments belonging to a specific patient. */
    List<Appointment> findByPatient_Id(int patientId);

    /** All appointments belonging to a specific doctor. */
    List<Appointment> findByDoctor_Id(int doctorId);

    // -------------------------------------------------------------------------
    // Conflict detection — used when CREATING a new appointment
    // (no exclusion needed; we want every ACTIVE appointment on that date)
    // -------------------------------------------------------------------------

    /**
     * Returns all ACTIVE appointments for a doctor on the given date.
     * Used to detect double-booking when creating a new appointment.
     */
    List<Appointment> findByDoctor_IdAndDateAndStatus(int doctorId, LocalDate date, Status status);

    /**
     * Returns all ACTIVE appointments for a patient on the given date.
     * Used to detect double-booking when creating a new appointment.
     */
    List<Appointment> findByPatient_IdAndDateAndStatus(int patientId, LocalDate date, Status status);

    // -------------------------------------------------------------------------
    // Conflict detection — used when UPDATING an existing appointment
    // (the appointment being edited is excluded so it doesn't conflict with itself)
    // -------------------------------------------------------------------------

    /**
     * Returns all ACTIVE appointments for a doctor on the given date,
     * excluding the appointment currently being edited.
     */
    List<Appointment> findByDoctor_IdAndDateAndStatusAndIdNot(int doctorId, LocalDate date, Status status, int excludeId);

    /**
     * Returns all ACTIVE appointments for a patient on the given date,
     * excluding the appointment currently being edited.
     */
    List<Appointment> findByPatient_IdAndDateAndStatusAndIdNot(int patientId, LocalDate date, Status status, int excludeId);

    // -------------------------------------------------------------------------
    // Auto-finish — find ACTIVE appointments whose time slot has already passed
    // -------------------------------------------------------------------------

    /**
     * Returns all ACTIVE appointments that should be marked FINISHED because
     * their end time has already passed.
     *
     * An appointment is considered expired when:
     *   - Its date is strictly before today, OR
     *   - Its date is today AND its end time is at or before the current time
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.status = :activeStatus " +
           "AND (a.date < :today " +
           "     OR (a.date = :today AND a.endTime <= :currentTime))")
    List<Appointment> findExpiredActiveAppointments(
            @Param("activeStatus") Status activeStatus,
            @Param("today") LocalDate today,
            @Param("currentTime") LocalTime currentTime
    );
}
