package com.HamesJoman.patient_portal.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AppointmentRequest DTO
 * Just making sure all the getters and setters on AppointmentRequest
 * work correctly and store the right values
 *
 * @author Mohamed Musa & Ali Beheshti
 */
class AppointmentRequestTest {

    /**
     * Test that all fields can be set and retrieved correctly
     * Covers date, start time, end time, patient ID, and doctor ID
     */
    @Test
    void testGettersAndSetters() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDate("2025-08-15");
        request.setStartTime("09:00");
        request.setEndTime("09:30");
        request.setPatientId(1);
        request.setDoctorId(2);

        assertEquals("2025-08-15", request.getDate());
        assertEquals("09:00", request.getStartTime());
        assertEquals("09:30", request.getEndTime());
        assertEquals(1, request.getPatientId());
        assertEquals(2, request.getDoctorId());
    }
}
