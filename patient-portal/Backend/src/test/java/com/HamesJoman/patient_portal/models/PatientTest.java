package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void testPatientConstructorAndGetters() {
        Patient patient = new Patient(1, "John", "Doe", "johndoe", "password123");
        
        assertEquals(1, patient.getId());
        assertEquals("John", patient.getFirstName());
        assertEquals("Doe", patient.getLastName());
        assertEquals("johndoe", patient.getUsername());
        assertEquals("password123", patient.getPassword());
        assertEquals("Patient", patient.getRole());
        assertNotNull(patient.getLastPasswordChange());
    }

    @Test
    void testSetters() {
        Patient patient = new Patient();
        patient.setId(2);
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        patient.setUsername("janesmith");
        patient.setPassword("newpass");
        patient.setRole("Admin");
        LocalDateTime now = LocalDateTime.now();
        patient.setLastLogin(now);

        assertEquals(2, patient.getId());
        assertEquals("Jane", patient.getFirstName());
        assertEquals("Smith", patient.getLastName());
        assertEquals("janesmith", patient.getUsername());
        assertEquals("newpass", patient.getPassword());
        assertEquals("Admin", patient.getRole());
        assertEquals(now, patient.getLastLogin());
    }
}
