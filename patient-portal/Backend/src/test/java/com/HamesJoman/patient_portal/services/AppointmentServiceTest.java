package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.dto.AppointmentRequest;
import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.repositories.AppointmentRepository;
import com.HamesJoman.patient_portal.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService
 *
 * Make sure you are using Mockito to mock the repos so we aren't using the actual
 * db or requiring any Spring context — we just want to test the service logic in isolation
 *
 * @author Mohamed Musa & Ali Beheshti
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    /**
     * Mocking the appointment repo so no real db calls are made
     */
    @Mock
    private AppointmentRepository appointmentRepository;

    /**
     * Mocking the user repo so we can control what patients and doctors get resolved
     * without touching the db
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Creates a real instance of AppointmentService and injects our
     * fake repos into it — so we're testing the actual service logic
     * without any of the infrastructure around it
     */
    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private AppointmentRequest request;

    /**
     * Runs before every single test
     * Sets up a fresh patient, doctor, and a valid appointment request each time
     * so tests don't bleed into each other
     */
    @BeforeEach
    void setUp() {
        patient = new Patient(1, "John", "Doe", "johndoe", "pass");
        doctor = new Doctor(2, "Dr.", "Smith", "drsmith", "pass");

        request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("09:00");
        request.setEndTime("10:00");
        request.setPatientId(1);
        request.setDoctorId(2);
    }

    /**
     * Test for successfully creating an appointment when there are no conflicts
     * Both the patient and doctor exist, no overlapping appointments, and the
     * saved appointment should come back with ACTIVE status
     */
    @Test
    void testCreateAppointmentSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor_IdAndDateAndStatus(anyInt(), any(), any())).thenReturn(new ArrayList<>());
        when(appointmentRepository.findByPatient_IdAndDateAndStatus(anyInt(), any(), any())).thenReturn(new ArrayList<>());
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.createAppointment(request);

        assertNotNull(result);
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(appointmentRepository).save(any());
    }

    /**
     * Test for creating an appointment where the end time is before the start time
     * The service should reject this before even hitting the repo
     */
    @Test
    void testCreateAppointmentInvalidTime() {
        request.setStartTime("10:00");
        request.setEndTime("09:00");

        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    /**
     * Test for creating an appointment that overlaps with an existing one for the same doctor
     * The repo returns a conflicting appointment, so the service should throw
     */
    @Test
    void testCreateAppointmentConflict() {
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2)).thenReturn(Optional.of(doctor));

        Appointment existing = new Appointment(LocalDate.parse("2025-08-15"), LocalTime.parse("09:30"), LocalTime.parse("10:30"), patient, doctor);
        ArrayList<Appointment> conflicts = new ArrayList<>();
        conflicts.add(existing);

        when(appointmentRepository.findByDoctor_IdAndDateAndStatus(anyInt(), any(), any())).thenReturn(conflicts);

        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    /**
     * Test for creating an appointment with a date string that can't be parsed
     * The service should throw before doing anything else
     */
    @Test
    void testCreateAppointmentInvalidDate() {
        request.setDate("invalid-date");
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    /**
     * Test for creating an appointment when the patient ID doesn't match any user
     * Service should throw since it can't resolve who the patient is
     */
    @Test
    void testResolvePatientNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    /**
     * Test for creating an appointment when the doctor ID doesn't match any user
     * Patient resolves fine but doctor lookup comes back empty, so the service should throw
     */
    @Test
    void testResolveDoctorNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    /**
     * Test for cancelling an appointment that exists and is ACTIVE
     * Should return the appointment with status flipped to CANCELLED
     */
    @Test
    void testCancelAppointment() {
        Appointment apt = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        apt.setStatus(Status.ACTIVE);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(apt));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.cancelAppointment(1);

        assertEquals(Status.CANCELLED, result.getStatus());
    }

    /**
     * Test for trying to cancel an appointment ID that doesn't exist
     * The repo returns empty, so the service should throw
     */
    @Test
    void testFindOrThrowNotFound() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> appointmentService.cancelAppointment(99));
    }

    /**
     * Test for successfully updating an existing ACTIVE appointment with new times
     * Should return the appointment with the updated date and start time
     */
    @Test
    void testUpdateAppointmentSuccess() {
        Appointment existing = new Appointment(LocalDate.parse("2025-08-15"), LocalTime.parse("08:00"), LocalTime.parse("09:00"), patient, doctor);
        existing.setStatus(Status.ACTIVE);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.updateAppointment(1, request);

        assertEquals(LocalDate.parse("2025-08-15"), result.getDate());
        assertEquals(LocalTime.parse("09:00"), result.getStartTime());
    }

    /**
     * Test for trying to update an appointment that is already CANCELLED
     * The service should reject updates on non-ACTIVE appointments
     */
    @Test
    void testUpdateAppointmentInvalidStatus() {
        Appointment existing = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        existing.setStatus(Status.CANCELLED);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> appointmentService.updateAppointment(1, request));
    }

    /**
     * Test for fetching all appointments belonging to a specific patient
     * Should return a non-null list and verify the repo was called with the right ID
     */
    @Test
    void testGetAppointmentsByPatient() {
        when(appointmentRepository.findByPatient_Id(1)).thenReturn(new ArrayList<>());
        
        List<Appointment> result = appointmentService.getAppointmentsByPatient(1);
        
        assertNotNull(result);
        verify(appointmentRepository).findByPatient_Id(1);
    }

    /**
     * Test for fetching all appointments belonging to a specific doctor
     * Should return a non-null list and verify the repo was called with the right ID
     */
    @Test
    void testGetAppointmentsByDoctor() {
        when(appointmentRepository.findByDoctor_Id(2)).thenReturn(new ArrayList<>());
        
        List<Appointment> result = appointmentService.getAppointmentsByDoctor(2);
        
        assertNotNull(result);
        verify(appointmentRepository).findByDoctor_Id(2);
    }

    /**
     * Test for fetching a single appointment by ID when it exists
     * Should return the exact appointment object the repo hands back
     */
    @Test
    void testGetAppointmentById() {
        Appointment apt = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(apt));
        
        Appointment result = appointmentService.getAppointmentById(1);
        
        assertEquals(apt, result);
    }
}
