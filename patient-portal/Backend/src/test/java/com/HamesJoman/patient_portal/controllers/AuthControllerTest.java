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

/**
 * Unit tests for AuthController
 * Make sure you are using Mockito to mock the service so we aren't touching
 * any real db logic or spinning up a Spring context — we just want to test
 * that the controller returns the right HTTP responses for login attempts
 *
 * @author Mohamed Musa & Ali Beheshti
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    /**
     * Mocking the service layer so the controller has something to talk to
     * without actually running any business logic or hitting the db
     */
    @Mock
    private UserService userService;

    /**
     * Creates a real instance of AuthController and injects our
     * fake service into it — so we're testing the actual controller code
     * without any of the infrastructure around it
     */
    @InjectMocks
    private AuthController authController;

    private Patient testUser;

    /**
     * Runs before every single test
     * Sets up a fresh patient each time so tests don't bleed into each other
     */
    @BeforeEach
    void setUp() {
        testUser = new Patient(1, "John", "Doe", "johndoe", "hashed_password");
    }

    /**
     * Test for a successful login where the username exists and the password matches
     * Should return 200 with the user in the body, and record the login
     */
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

    /**
     * Test for login when the username doesn't exist in the system
     * Service returns null for an unknown user, so we expect a 401 with no body
     */
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

    /**
     * Test for login when the username exists but the password is wrong
     * verifyPassword comes back false, so we expect a 401 with no body
     */
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
