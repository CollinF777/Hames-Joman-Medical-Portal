package com.HamesJoman.patient_portal.controllers;

import com.HamesJoman.patient_portal.dto.UserRequest;
import com.HamesJoman.patient_portal.models.Patient;
import com.HamesJoman.patient_portal.models.User;
import com.HamesJoman.patient_portal.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController
 *
 * Make sure you are using Mockito to mock the service so we aren't touching
 * any real db logic or spinning up a Spring context — we just want to test
 * that the controller returns the right responses for user operations
 *
 * @author Mohamed Musa & Ali Behesthi
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    /**
     * Mocking the service layer so the controller has something to talk to
     * without actually running any business logic or hitting the db
     */
    @Mock
    private UserService userService;

    /**
     * Creates a real instance of UserController and injects our
     * fake service into it — so we're testing the actual controller code
     * without any of the infrastructure around it
     */
    @InjectMocks
    private UserController userController;

    /**
     * Test for getting all users and expecting the full list back
     * Also verifies the service method was actually called
     */
    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new Patient(1, "John", "Doe", "j", "p"));
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(1, result.size());
        verify(userService).getAllUsers();
    }

    /**
     * Test for fetching a user by ID when that user exists
     * Should return 200 with the user's details in the body
     */
    @Test
    void testGetUserByIdSuccess() {
        Patient patient = new Patient(1, "John", "Doe", "j", "p");
        when(userService.getUser(1)).thenReturn(patient);

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("John", response.getBody().getFirstName());
    }

    /**
     * Test for fetching a user by ID when that user doesn't exist
     * Service returns null, so we expect a 404
     */
    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUser(99)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(99);

        assertEquals(404, response.getStatusCode().value());
    }

    /**
     * Test for creating a new user with all required fields filled in
     * Should return the created user with the right username
     */
    @Test
    void testCreateUser() {
        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("johndoe");
        request.setRole("patient");
        request.setPassword("pass");

        Patient created = new Patient(1, "John", "Doe", "johndoe", "pass");
        when(userService.createUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(created);

        User result = userController.createUser(request);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
    }

    /**
     * Test for successfully updating an existing user's details
     * Should return 200 with the updated user in the body
     */
    @Test
    void testUpdateUserSuccess() {
        UserRequest request = new UserRequest();
        Patient updated = new Patient(1, "New", "Name", "un", "pw");
        when(userService.updateUser(eq(1), any(UserRequest.class))).thenReturn(updated);

        ResponseEntity<User> response = userController.updateUser(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("New", response.getBody().getFirstName());
    }

    /**
     * Test for deleting a user that exists
     * Service returns true on success, so we just verify that comes through
     */
    @Test
    void testDeleteUser() {
        when(userService.deleteUser(1)).thenReturn(true);
        Boolean result = userController.deleteUser(1);
        assertTrue(result);
    }
}
