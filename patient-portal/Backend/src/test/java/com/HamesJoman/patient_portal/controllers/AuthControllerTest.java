package com.HamesJoman.patient_portal.controllers;

import com.HamesJoman.patient_portal.dto.LoginRequest;
import com.HamesJoman.patient_portal.models.Patient;
import com.HamesJoman.patient_portal.models.User;
import com.HamesJoman.patient_portal.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private Patient testUser;

    @BeforeEach
    void setUp() {
        testUser = new Patient(1, "John", "Doe", "johndoe", "hashed_password");
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("johndoe");
        request.setPassword("password");

        when(userService.getUserByUsername("johndoe")).thenReturn(testUser);
        when(userService.verifyPassword("password", "hashed_password")).thenReturn(true);

        ResponseEntity<User> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("johndoe", response.getBody().getUsername());
        verify(userService).recordLogin(1);
    }

    @Test
    void testLoginFailureUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("password");

        when(userService.getUserByUsername("unknown")).thenReturn(null);

        ResponseEntity<User> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testLoginFailureWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("johndoe");
        request.setPassword("wrong");

        when(userService.getUserByUsername("johndoe")).thenReturn(testUser);
        when(userService.verifyPassword("wrong", "hashed_password")).thenReturn(false);

        ResponseEntity<User> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
