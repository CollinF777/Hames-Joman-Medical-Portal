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

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new Patient(1, "John", "Doe", "j", "p"));
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(1, result.size());
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserByIdSuccess() {
        Patient patient = new Patient(1, "John", "Doe", "j", "p");
        when(userService.getUser(1)).thenReturn(patient);

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("John", response.getBody().getFirstName());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUser(99)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(99);

        assertEquals(404, response.getStatusCode().value());
    }

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

    @Test
    void testUpdateUserSuccess() {
        UserRequest request = new UserRequest();
        Patient updated = new Patient(1, "New", "Name", "un", "pw");
        when(userService.updateUser(eq(1), any(UserRequest.class))).thenReturn(updated);

        ResponseEntity<User> response = userController.updateUser(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("New", response.getBody().getFirstName());
    }

    @Test
    void testDeleteUser() {
        when(userService.deleteUser(1)).thenReturn(true);
        Boolean result = userController.deleteUser(1);
        assertTrue(result);
    }
}
