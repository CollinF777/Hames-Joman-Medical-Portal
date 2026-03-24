package com.HamesJoman.patient_portal.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @Test
    void testGettersAndSetters() {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("First");
        userRequest.setLastName("Last");
        userRequest.setUsername("user123");
        userRequest.setPassword("pass123");
        userRequest.setRole("Doctor");

        assertEquals("First", userRequest.getFirstName());
        assertEquals("Last", userRequest.getLastName());
        assertEquals("user123", userRequest.getUsername());
        assertEquals("pass123", userRequest.getPassword());
        assertEquals("Doctor", userRequest.getRole());
    }
}
