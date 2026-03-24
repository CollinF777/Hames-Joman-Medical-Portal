package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

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

    @Test
    void testDefaultConstructor() {
        Doctor doctor = new Doctor();
        assertNotNull(doctor);
    }
}
