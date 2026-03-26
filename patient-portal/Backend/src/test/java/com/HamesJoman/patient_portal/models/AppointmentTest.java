package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Appointment model
 * Making sure the constructors, getters, and setters all work as expected
 * and that Appointment-specific defaults like status are set correctly
 *
 * @author Mohamed Musa & Ali Beheshti
 */
class AppointmentTest {

    /**
     * Test that the full constructor sets all fields correctly
     * Also checks that status defaults to ACTIVE on a new appointment
     */
    @Test
    void testAppointmentConstructorAndGetters() {
        LocalDate date = LocalDate.of(2023, 10, 20);
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(11, 0);
        Patient patient = new Patient();
        Doctor doctor = new Doctor();

        Appointment appointment = new Appointment(date, start, end, patient, doctor);
        
        assertEquals(date, appointment.getDate());
        assertEquals(start, appointment.getStartTime());
        assertEquals(end, appointment.getEndTime());
        assertEquals(patient, appointment.getPatient());
        assertEquals(doctor, appointment.getDoctor());
        assertEquals(Status.ACTIVE, appointment.getStatus());
    }

    /**
     * Test that all fields can be individually set and retrieved correctly
     * Covers id, date, times, patient, doctor, status, and lastUpdated
     */
    @Test
    void testSetters() {
        Appointment appointment = new Appointment();
        LocalDate date = LocalDate.of(2023, 10, 21);
        LocalTime start = LocalTime.of(14, 0);
        LocalTime end = LocalTime.of(15, 0);
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        LocalDateTime updated = LocalDateTime.now();

        appointment.setId(100);
        appointment.setDate(date);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(Status.CANCELLED);
        appointment.setLastUpdated(updated);

        assertEquals(100, appointment.getId());
        assertEquals(date, appointment.getDate());
        assertEquals(start, appointment.getStartTime());
        assertEquals(end, appointment.getEndTime());
        assertEquals(patient, appointment.getPatient());
        assertEquals(doctor, appointment.getDoctor());
        assertEquals(Status.CANCELLED, appointment.getStatus());
        assertEquals(updated, appointment.getLastUpdated());
    }
}
