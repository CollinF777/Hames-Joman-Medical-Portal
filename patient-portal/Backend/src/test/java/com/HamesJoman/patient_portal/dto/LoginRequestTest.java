package com.HamesJoman.patient_portal.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginRequest DTO
 * Just making sure all the getters and setters on LoginRequest
 * work correctly and store the right values
 *
 * @author Mohamed Musa & Ali Beheshti
 */
class LoginRequestTest {

    /**
     * Test that username and password can be set and retrieved correctly
     */
    @Test
    void testGettersAndSetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("testpass", loginRequest.getPassword());
    }
}
