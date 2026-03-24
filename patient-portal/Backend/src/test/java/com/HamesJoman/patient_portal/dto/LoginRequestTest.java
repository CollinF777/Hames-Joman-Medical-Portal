package com.HamesJoman.patient_portal.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testGettersAndSetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("testpass", loginRequest.getPassword());
    }
}
