package com.HamesJoman.patient_portal.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentRequestTest {

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
