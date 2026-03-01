package com.HamesJoman.patient_portal.controllers;

import com.HamesJoman.patient_portal.dto.AppointmentRequest;
import com.HamesJoman.patient_portal.models.Appointment;
import com.HamesJoman.patient_portal.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Appointment entities via HTTP endpoints.
 * Provides endpoints for creating, reading, updating, and cancelling appointments.
 *
 * Appointments are NEVER deleted from the database. Cancellations are tracked
 * via status, and completed appointments are automatically marked FINISHED.
 *
 * All endpoints are prefixed with /api/appointments
 *
 * @author Collin Fair
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // =========================================================================
    // CREATE
    // =========================================================================

    /**
     * POST /api/appointments
     * Creates a new appointment.
     *
     * Request body fields:
     *   date      — "YYYY-MM-DD"  (e.g. "2025-08-15")
     *   startTime — "HH:mm"       (e.g. "09:00")
     *   endTime   — "HH:mm"       (e.g. "09:30")
     *   patientId — int
     *   doctorId  — int
     *
     * @param request DTO containing appointment details
     * @return 201 CREATED with the new Appointment, or 400/409 on validation failure
     */
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest request) {
        // Validate required fields
        if (request.getDate() == null || request.getDate().isBlank() ||
            request.getStartTime() == null || request.getStartTime().isBlank() ||
            request.getEndTime() == null || request.getEndTime().isBlank() ||
            request.getPatientId() == 0 || request.getDoctorId() == 0) {
            return ResponseEntity.badRequest().body("Please fill in all fields");
        }

        try {
            Appointment created = appointmentService.createAppointment(request);
            return ResponseEntity.status(201).body(created);
        } catch (IllegalArgumentException e) {
            // Conflict or invalid reference (bad patient/doctor id, time overlap, etc.)
            String message = e.getMessage();
            if (message != null && message.startsWith("Time conflict")) {
                return ResponseEntity.status(409).body(message);
            }
            return ResponseEntity.badRequest().body(message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(500).body("Server error while creating appointment");
        }
    }

    // =========================================================================
    // READ
    // =========================================================================

    /**
     * GET /api/appointments
     * Retrieves every appointment in the system (admin use).
     * Expired ACTIVE appointments are automatically marked FINISHED before returning.
     *
     * @return 200 OK with a list of all appointments
     */
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * GET /api/appointments/{id}
     * Retrieves a single appointment by its ID.
     *
     * @param id The unique identifier of the appointment
     * @return 200 OK with the Appointment, or 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable int id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ResponseEntity.status(404).body("Appointment not found with id: " + id);
        }
        return ResponseEntity.ok(appointment);
    }

    /**
     * GET /api/appointments/patient/{patientId}
     * Retrieves all appointments for a specific patient.
     *
     * @param patientId The ID of the patient
     * @return 200 OK with a list of the patient's appointments
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable int patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    /**
     * GET /api/appointments/doctor/{doctorId}
     * Retrieves all appointments for a specific doctor.
     *
     * @param doctorId The ID of the doctor
     * @return 200 OK with a list of the doctor's appointments
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable int doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    /**
     * PUT /api/appointments/{id}
     * Updates (reschedules) an existing ACTIVE appointment.
     * Only ACTIVE appointments can be rescheduled — attempting to update
     * a CANCELLED or FINISHED appointment will return 400.
     *
     * Request body fields (all required):
     *   date      — "YYYY-MM-DD"
     *   startTime — "HH:mm"
     *   endTime   — "HH:mm"
     *   patientId — int
     *   doctorId  — int
     *
     * @param id      The ID of the appointment to update
     * @param request DTO containing the updated appointment details
     * @return 200 OK with the updated Appointment, or 400/404/409 on failure
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable int id, @RequestBody AppointmentRequest request) {
        if (request.getDate() == null || request.getDate().isBlank() ||
            request.getStartTime() == null || request.getStartTime().isBlank() ||
            request.getEndTime() == null || request.getEndTime().isBlank() ||
            request.getPatientId() == 0 || request.getDoctorId() == 0) {
            return ResponseEntity.badRequest().body("Please fill in all fields");
        }

        try {
            Appointment updated = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message != null && message.contains("not found")) {
                return ResponseEntity.status(404).body(message);
            }
            if (message != null && message.startsWith("Time conflict")) {
                return ResponseEntity.status(409).body(message);
            }
            return ResponseEntity.badRequest().body(message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(500).body("Server error while updating appointment");
        }
    }

    /**
     * PUT /api/appointments/{id}/cancel
     * Cancels an ACTIVE appointment by setting its status to CANCELLED.
     * Already-CANCELLED or FINISHED appointments cannot be cancelled.
     *
     * @param id The ID of the appointment to cancel
     * @return 200 OK with the cancelled Appointment, or 400/404 on failure
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable int id) {
        try {
            Appointment cancelled = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message != null && message.contains("not found")) {
                return ResponseEntity.status(404).body(message);
            }
            return ResponseEntity.badRequest().body(message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(500).body("Server error while cancelling appointment");
        }
    }
}
