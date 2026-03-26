package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Patient model
 * Making sure the constructors, getters, and setters all work as expected
 * and that Patient-specific defaults like role are set correctly
 *
 * @author Mohamed Musa & Ali Beheshti
 */
class PatientTest {

    /**
     * Test that the full constructor sets all fields correctly
     * Also checks that role defaults to "Patient" and lastPasswordChange is set automatically
     */
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

    /**
     * Test that all fields can be individually set and retrieved correctly
     * Covers id, name, username, password, role, and lastLogin
     */
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
