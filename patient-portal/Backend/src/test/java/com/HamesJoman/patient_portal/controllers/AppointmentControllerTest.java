package com.HamesJoman.patient_portal.controllers;

import com.HamesJoman.patient_portal.dto.AppointmentRequest;
import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit for AppointmentController
 * Make sure you are using Mockito to mock the service so we aren't touching
 * any real db logic or spinning up a spring context - we just want to test
 * that the controller returns the right HTTP response
 *
 * @author Mohamed Musa & Ali Beheshti
 */

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {
    /**
     * Mocking the service layer so the controller has something to talk to
     * without actually running any business logic for hitting the db
     */
    @Mock
    private AppointmentService appointmentService;

    /**
     * Create a real instance of AppointmentController and injects our
     * fake service into it - so we're testing the actual controller code
     * without any of the infrastructure around it
     */

    @InjectMocks
    private AppointmentController appointmentController;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;

    /**
     * Runs before every single test
     * sets up a fresh patient, doctor, and appointment each time so
     * tests don't bleed into each other
     */

    @BeforeEach
    void setUp() {
        patient = new Patient(1, "John", "Doe", "j", "p");
        doctor = new Doctor(2, "Dr.", "Smith", "d", "p");
        appointment = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        appointment.setId(10);
    }

    /**
     * We're asking for all appointments and expecting to get back the one
     * we set up, with 200 ok
     */
    @Test
    void testGetAllAppointments() {
        // Tell the fake service what to return when asked for all appointments
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(appointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(10, response.getBody().get(0).getId());
    }

    /**
     * Test for creating an appointment when all the required fields are filed in
     * should come back ith a 201 Created and the appointment in the body
     */

    @Test
    void testCreateAppointment() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setPatientId(1);
        request.setDoctorId(2);
        // Service saves it and hands it back
        when(appointmentService.createAppointment(any())).thenReturn(appointment);

        ResponseEntity<?> response = appointmentController.createAppointment(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(appointment, response.getBody());
    }

    /**
     * Test for cancelling an appointment that exists and is currently ACTIVE
     * Should return 200 with the updated appointment in the body
     */
    @Test
    void testCancelAppointment() {
        when(appointmentService.cancelAppointment(10)).thenReturn(appointment);

        ResponseEntity<?> response = appointmentController.cancelAppointment(10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(appointment, response.getBody());
    }

    /**
     * Test for fetching all appointments belonging to a specific patient
     * Should return 200 and a list with the right number of appointments
     */
    @Test
    void testGetByPatient() {
        when(appointmentService.getAppointmentsByPatient(1)).thenReturn(Arrays.asList(appointment));

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByPatient(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Test for fetching all appointments belonging to a specific doctor
     * Same idea as getByPatient just filtered by doctor ID instead
     */
    @Test
    void testGetByDoctor() {
        when(appointmentService.getAppointmentsByDoctor(2)).thenReturn(Arrays.asList(appointment));

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDoctor(2);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Test for looking up a specific appointment by ID when it doesn't exist
     * Service returns null and the controller should respond with a 404
     */
    @Test
    void testGetByIdNotFound() {
        when(appointmentService.getAppointmentById(99)).thenReturn(null);

        ResponseEntity<?> response = appointmentController.getAppointmentById(99);

        assertEquals(404, response.getStatusCode().value());
    }

    /**
     * Test for what happens when the request is missing required fields like
     * date and times — controller should catch this before even calling the service
     * and return a 400 with a helpful message
     */
    @Test
    void testCreateAppointmentMissingFields() {
        AppointmentRequest request = new AppointmentRequest();
        // Missing date/times

        ResponseEntity<?> response = appointmentController.createAppointment(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Please fill in all fields", response.getBody());
    }


    /**
     * Test for when the appointment overlaps with an existing one
     * The service throws an IllegalArgumentException with "Time conflict" in the message
     * and the controller should translate that into a 409 Conflict
     */
    @Test
    void testCreateAppointmentConflict() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setPatientId(1);
        request.setDoctorId(2);

        when(appointmentService.createAppointment(any())).thenThrow(new IllegalArgumentException("Time conflict: ..."));

        ResponseEntity<?> response = appointmentController.createAppointment(request);

        assertEquals(409, response.getStatusCode().value());
    }

    /**
     * Test for successfully updating an existing appointment
     * All fields are provided and the service returns the updated appointment,
     * so we're expecting a clean 200 back
     */
    @Test
    void testUpdateAppointmentSuccess() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("10:00");
        request.setEndTime("11:00");
        request.setPatientId(1);
        request.setDoctorId(2);

        when(appointmentService.updateAppointment(eq(10), any())).thenReturn(appointment);

        ResponseEntity<?> response = appointmentController.updateAppointment(10, request);

        assertEquals(200, response.getStatusCode().value());
    }

    /**
     * Test for trying to update an appointment that doesn't exist
     * Service throws IllegalArgumentException with "Appointment not found",
     * and the controller should map that to a 404
     */
    @Test
    void testUpdateAppointmentNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("10:00");
        request.setEndTime("11:00");
        request.setPatientId(1);
        request.setDoctorId(2);

        when(appointmentService.updateAppointment(eq(99), any())).thenThrow(new IllegalArgumentException("Appointment not found"));

        ResponseEntity<?> response = appointmentController.updateAppointment(99, request);

        assertEquals(404, response.getStatusCode().value());
    }

    /**
     * Test for trying to cancel an appointment that doesn't exist
     * Service throws with "not found" in the message, controller should return 404
     */
    @Test
    void testCancelAppointmentNotFound() {
        when(appointmentService.cancelAppointment(99)).thenThrow(new IllegalArgumentException("not found"));
        ResponseEntity<?> response = appointmentController.cancelAppointment(99);
        assertEquals(404, response.getStatusCode().value());
    }

    /**
     * Test for trying to cancel an appointment that isn't ACTIVE anymore
     * (e.g. already canceled or completed)
     * Service throws with "Only ACTIVE" in the message, controller should return 400
     */
    @Test
    void testCancelAppointmentBadRequest() {
        when(appointmentService.cancelAppointment(10)).thenThrow(new IllegalArgumentException("Only ACTIVE..."));
        ResponseEntity<?> response = appointmentController.cancelAppointment(10);
        assertEquals(400, response.getStatusCode().value());
    }

    /**
     * Test for when something completely unexpected blows up in the service
     * The controller should catch it and return a 500 instead of just crashing
     */
    @Test
    void testCreateAppointmentGeneralFailure() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setPatientId(1);
        request.setDoctorId(2);

        when(appointmentService.createAppointment(any())).thenThrow(new RuntimeException("Oops"));
        ResponseEntity<?> response = appointmentController.createAppointment(request);
        assertEquals(500, response.getStatusCode().value());
    }
}
