package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

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
