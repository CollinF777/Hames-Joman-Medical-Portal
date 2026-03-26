package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Doctor model
 * Making sure the constructors and getters work as expected
 * and that role defaults to "Doctor" when using the full constructor
 *
 * @author Mohamed Musa & Ali Beheshti
 */
class DoctorTest {

    /**
     * Test that the full constructor sets all fields correctly
     * Also checks that role defaults to "Doctor"
     */
    @Test
    void testDoctorConstructorAndGetters() {
        Doctor doctor = new Doctor(1, "Gregory", "House", "ghouse", "lupus");
        
        assertEquals(1, doctor.getId());
        assertEquals("Gregory", doctor.getFirstName());
        assertEquals("House", doctor.getLastName());
        assertEquals("ghouse", doctor.getUsername());
        assertEquals("lupus", doctor.getPassword());
        assertEquals("Doctor", doctor.getRole());
    }

    /**
     * Test that the default no-arg constructor creates a non-null Doctor object
     */
    @Test
    void testDefaultConstructor() {
        Doctor doctor = new Doctor();
        assertNotNull(doctor);
    }
}
