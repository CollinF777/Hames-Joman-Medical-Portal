package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.dto.AppointmentRequest;
import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.repositories.AppointmentRepository;
import com.HamesJoman.patient_portal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service layer for managing Appointment entities.
 * Handles all business logic for appointments including:
 *   - Creating new appointments with double-booking prevention
 *   - Updating existing appointments (rescheduling, reassigning)
 *   - Cancelling appointments (status -> CANCELLED)
 *   - Auto-finishing appointments whose time slot has passed (status -> FINISHED)
 *
 * Appointments are NEVER deleted from the database. Cancellations and
 * completions are tracked via the Status enum.
 *
 * @author Collin Fair
 */
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Creates a new appointment after validating that:
     *   1. The referenced patient and doctor exist.
     *   2. The time slot does not conflict with any existing ACTIVE appointment
     *      for either the doctor or the patient on the same date.
     *
     * @param request DTO containing date, startTime, endTime, patientId, doctorId
     * @return The persisted Appointment with status ACTIVE
     * @throws IllegalArgumentException if the patient or doctor cannot be found,
     *                                  or if a time conflict exists
     */
    public Appointment createAppointment(AppointmentRequest request) {
        LocalDate date      = LocalDate.parse(request.getDate());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime   = LocalTime.parse(request.getEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Patient patient = resolvePatient(request.getPatientId());
        Doctor  doctor  = resolveDoctor(request.getDoctorId());

        // Check for scheduling conflicts (no exclusion — brand new appointment)
        checkForConflicts(date, startTime, endTime, doctor.getId(), patient.getId(), -1);

        Appointment appointment = new Appointment(date, startTime, endTime, patient, doctor);
        return appointmentRepository.save(appointment);
    }

    /**
     * Updates an existing appointment's date, time, patient, and/or doctor.
     * The appointment being edited is excluded from the conflict check so that
     * it does not conflict with its own previous time slot.
     *
     * Only ACTIVE appointments may be rescheduled. Attempting to update a
     * CANCELLED or FINISHED appointment will throw an exception.
     *
     * @param id      The ID of the appointment to update
     * @param request DTO containing the new field values
     * @return The updated, persisted Appointment
     * @throws IllegalArgumentException if the appointment does not exist,
     *                                  is not ACTIVE, or if a conflict is detected
     */
    public Appointment updateAppointment(int id, AppointmentRequest request) {
        Appointment existing = findOrThrow(id);

        if (existing.getStatus() != Status.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only ACTIVE appointments can be rescheduled. " +
                    "Current status: " + existing.getStatus()
            );
        }

        LocalDate date      = LocalDate.parse(request.getDate());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime   = LocalTime.parse(request.getEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Patient patient = resolvePatient(request.getPatientId());
        Doctor  doctor  = resolveDoctor(request.getDoctorId());

        // Exclude this appointment's own ID so it doesn't block itself
        checkForConflicts(date, startTime, endTime, doctor.getId(), patient.getId(), id);

        existing.setDate(date);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);
        existing.setPatient(patient);
        existing.setDoctor(doctor);

        return appointmentRepository.save(existing);
    }

    /**
     * Marks an appointment as CANCELLED.
     * Already-CANCELLED or FINISHED appointments cannot be cancelled again.
     *
     * @param id The ID of the appointment to cancel
     * @return The updated, persisted Appointment with status CANCELLED
     * @throws IllegalArgumentException if the appointment does not exist or
     *                                  is not currently ACTIVE
     */
    public Appointment cancelAppointment(int id) {
        Appointment appointment = findOrThrow(id);

        if (appointment.getStatus() != Status.ACTIVE) {
            throw new IllegalArgumentException(
                    "Only ACTIVE appointments can be cancelled. " +
                    "Current status: " + appointment.getStatus()
            );
        }

        appointment.setStatus(Status.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments in the system (intended for admin use).
     * Before returning, expired ACTIVE appointments are automatically
     * transitioned to FINISHED.
     *
     * @return List of every appointment regardless of status
     */
    public List<Appointment> getAllAppointments() {
        markExpiredAppointmentsAsFinished();
        return appointmentRepository.findAll();
    }

    /**
     * Retrieves all appointments for a specific patient.
     * Expired ACTIVE appointments are automatically marked FINISHED first.
     *
     * @param patientId The ID of the patient
     * @return List of appointments belonging to the patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        markExpiredAppointmentsAsFinished();
        return appointmentRepository.findByPatient_Id(patientId);
    }

    /**
     * Retrieves all appointments for a specific doctor.
     * Expired ACTIVE appointments are automatically marked FINISHED first.
     *
     * @param doctorId The ID of the doctor
     * @return List of appointments belonging to the doctor
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        markExpiredAppointmentsAsFinished();
        return appointmentRepository.findByDoctor_Id(doctorId);
    }

    /**
     * Retrieves a single appointment by its ID.
     * Expired ACTIVE appointments are automatically marked FINISHED first.
     *
     * @param id The appointment's unique identifier
     * @return The Appointment if found, or null if it does not exist
     */
    public Appointment getAppointmentById(int id) {
        markExpiredAppointmentsAsFinished();
        return appointmentRepository.findById(id).orElse(null);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    /**
     * Checks whether the proposed time slot overlaps with any existing ACTIVE
     * appointment for the given doctor or patient on the same date.
     *
     * When updating an existing appointment, pass its ID as {@code excludeId}
     * so the appointment does not conflict with itself. Pass {@code -1} when
     * creating a brand-new appointment.
     *
     * @param date       The appointment date
     * @param startTime  Proposed start time
     * @param endTime    Proposed end time
     * @param doctorId   ID of the doctor being booked
     * @param patientId  ID of the patient being booked
     * @param excludeId  ID to exclude from conflict search (-1 = no exclusion)
     * @throws IllegalArgumentException if any conflict is found
     */
    private void checkForConflicts(LocalDate date, LocalTime startTime, LocalTime endTime,
                                   int doctorId, int patientId, int excludeId) {
        List<Appointment> doctorApts;
        List<Appointment> patientApts;

        if (excludeId < 0) {
            // Creating — check all ACTIVE appointments on this date
            doctorApts  = appointmentRepository.findByDoctor_IdAndDateAndStatus(doctorId, date, Status.ACTIVE);
            patientApts = appointmentRepository.findByPatient_IdAndDateAndStatus(patientId, date, Status.ACTIVE);
        } else {
            // Updating — exclude the appointment being edited
            doctorApts  = appointmentRepository.findByDoctor_IdAndDateAndStatusAndIdNot(doctorId, date, Status.ACTIVE, excludeId);
            patientApts = appointmentRepository.findByPatient_IdAndDateAndStatusAndIdNot(patientId, date, Status.ACTIVE, excludeId);
        }

        for (Appointment apt : doctorApts) {
            if (timesOverlap(startTime, endTime, apt.getStartTime(), apt.getEndTime())) {
                throw new IllegalArgumentException(
                        "Time conflict: Doctor already has an appointment from " +
                        apt.getStartTime() + " to " + apt.getEndTime() + " on " + date
                );
            }
        }

        for (Appointment apt : patientApts) {
            if (timesOverlap(startTime, endTime, apt.getStartTime(), apt.getEndTime())) {
                throw new IllegalArgumentException(
                        "Time conflict: Patient already has an appointment from " +
                        apt.getStartTime() + " to " + apt.getEndTime() + " on " + date
                );
            }
        }
    }

    /**
     * Determines whether two time ranges overlap.
     * Two ranges overlap when start1 is before end2 AND start2 is before end1.
     * Back-to-back appointments (end1 == start2) are NOT considered overlapping.
     *
     * @return true if the ranges overlap, false otherwise
     */
    private boolean timesOverlap(LocalTime start1, LocalTime end1,
                                 LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Scans the database for any ACTIVE appointments whose end time has already
     * passed and bulk-updates them to FINISHED.
     *
     * This is called lazily at the start of every read operation so the UI
     * always sees up-to-date statuses without requiring a background scheduler.
     */
    private void markExpiredAppointmentsAsFinished() {
        LocalDate today       = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<Appointment> expired = appointmentRepository
                .findExpiredActiveAppointments(Status.ACTIVE, today, currentTime);

        if (!expired.isEmpty()) {
            expired.forEach(apt -> apt.setStatus(Status.FINISHED));
            appointmentRepository.saveAll(expired);
        }
    }

    /**
     * Finds an appointment by ID or throws if it does not exist.
     *
     * @param id The appointment ID to look up
     * @return The found Appointment
     * @throws IllegalArgumentException if no appointment with that ID exists
     */
    private Appointment findOrThrow(int id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));
    }

    /**
     * Finds a Patient by ID or throws if not found or wrong type.
     *
     * @param patientId The user ID expected to be a Patient
     * @return The Patient entity
     * @throws IllegalArgumentException if no user exists or the user is not a Patient
     */
    private Patient resolvePatient(int patientId) {
        return userRepository.findById(patientId)
                .filter(u -> u instanceof Patient)
                .map(u -> (Patient) u)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId));
    }

    /**
     * Finds a Doctor by ID or throws if not found or wrong type.
     *
     * @param doctorId The user ID expected to be a Doctor
     * @return The Doctor entity
     * @throws IllegalArgumentException if no user exists or the user is not a Doctor
     */
    private Doctor resolveDoctor(int doctorId) {
        return userRepository.findById(doctorId)
                .filter(u -> u instanceof Doctor)
                .map(u -> (Doctor) u)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + doctorId));
    }
}
