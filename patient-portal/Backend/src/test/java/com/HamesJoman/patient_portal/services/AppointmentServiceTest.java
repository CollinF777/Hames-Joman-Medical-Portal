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

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private AppointmentRequest request;

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

    @Test
    void testCreateAppointmentInvalidTime() {
        request.setStartTime("10:00");
        request.setEndTime("09:00");

        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

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

    @Test
    void testCancelAppointment() {
        Appointment apt = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        apt.setStatus(Status.ACTIVE);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(apt));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.cancelAppointment(1);

        assertEquals(Status.CANCELLED, result.getStatus());
    }

    @Test
    void testCreateAppointmentInvalidDate() {
        request.setDate("invalid-date");
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

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

    @Test
    void testUpdateAppointmentInvalidStatus() {
        Appointment existing = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        existing.setStatus(Status.CANCELLED);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> appointmentService.updateAppointment(1, request));
    }

    @Test
    void testGetAppointmentsByPatient() {
        when(appointmentRepository.findByPatient_Id(1)).thenReturn(new ArrayList<>());
        
        List<Appointment> result = appointmentService.getAppointmentsByPatient(1);
        
        assertNotNull(result);
        verify(appointmentRepository).findByPatient_Id(1);
    }

    @Test
    void testGetAppointmentsByDoctor() {
        when(appointmentRepository.findByDoctor_Id(2)).thenReturn(new ArrayList<>());
        
        List<Appointment> result = appointmentService.getAppointmentsByDoctor(2);
        
        assertNotNull(result);
        verify(appointmentRepository).findByDoctor_Id(2);
    }

    @Test
    void testGetAppointmentById() {
        Appointment apt = new Appointment(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), patient, doctor);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(apt));
        
        Appointment result = appointmentService.getAppointmentById(1);
        
        assertEquals(apt, result);
    }

    @Test
    void testResolvePatientNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    void testResolveDoctorNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    void testFindOrThrowNotFound() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> appointmentService.cancelAppointment(99));
    }
}
