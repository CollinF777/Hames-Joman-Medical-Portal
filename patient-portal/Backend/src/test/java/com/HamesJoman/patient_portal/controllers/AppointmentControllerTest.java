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

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new Patient(1, "John", "Doe", "j", "p");
        doctor = new Doctor(2, "Dr.", "Smith", "d", "p");
        appointment = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        appointment.setId(10);
    }

    @Test
    void testGetAllAppointments() {
        when(appointmentService.getAllAppointments()).thenReturn(Arrays.asList(appointment));
        
        ResponseEntity<List<Appointment>> response = appointmentController.getAllAppointments();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(10, response.getBody().get(0).getId());
    }

    @Test
    void testCreateAppointment() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setPatientId(1);
        request.setDoctorId(2);
        
        when(appointmentService.createAppointment(any())).thenReturn(appointment);

        ResponseEntity<?> response = appointmentController.createAppointment(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(appointment, response.getBody());
    }

    @Test
    void testCancelAppointment() {
        when(appointmentService.cancelAppointment(10)).thenReturn(appointment);

        ResponseEntity<?> response = appointmentController.cancelAppointment(10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(appointment, response.getBody());
    }

    @Test
    void testGetByPatient() {
        when(appointmentService.getAppointmentsByPatient(1)).thenReturn(Arrays.asList(appointment));

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByPatient(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetByDoctor() {
        when(appointmentService.getAppointmentsByDoctor(2)).thenReturn(Arrays.asList(appointment));

        ResponseEntity<List<Appointment>> response = appointmentController.getAppointmentsByDoctor(2);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetByIdNotFound() {
        when(appointmentService.getAppointmentById(99)).thenReturn(null);

        ResponseEntity<?> response = appointmentController.getAppointmentById(99);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCreateAppointmentMissingFields() {
        AppointmentRequest request = new AppointmentRequest();
        // Missing date/times

        ResponseEntity<?> response = appointmentController.createAppointment(request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Please fill in all fields", response.getBody());
    }

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

    @Test
    void testCancelAppointmentNotFound() {
        when(appointmentService.cancelAppointment(99)).thenThrow(new IllegalArgumentException("not found"));
        ResponseEntity<?> response = appointmentController.cancelAppointment(99);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCancelAppointmentBadRequest() {
        when(appointmentService.cancelAppointment(10)).thenThrow(new IllegalArgumentException("Only ACTIVE..."));
        ResponseEntity<?> response = appointmentController.cancelAppointment(10);
        assertEquals(400, response.getStatusCode().value());
    }

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
