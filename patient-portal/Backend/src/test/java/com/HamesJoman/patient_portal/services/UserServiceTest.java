package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.models.Admin;
import com.HamesJoman.patient_portal.models.Doctor;
import com.HamesJoman.patient_portal.models.Patient;
import com.HamesJoman.patient_portal.models.User;
import com.HamesJoman.patient_portal.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.createUser("John", "Doe", "johndoe", "password", "patient");

        assertNotNull(user);
        assertTrue(user instanceof Patient);
        assertEquals("John", user.getFirstName());
        assertEquals("johndoe", user.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateDoctorSuccess() {
        when(userRepository.existsByUsername("drhouse")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.createUser("Gregory", "House", "drhouse", "lupus", "doctor");

        assertTrue(user instanceof Doctor);
        assertEquals("Doctor", user.getRole());
    }

    @Test
    void testCreateAdminSuccess() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.createUser("Lisa", "Cuddy", "admin", "password", "admin");

        assertTrue(user instanceof Admin);
        assertEquals("Admin", user.getRole());
    }

    @Test
    void testCreateUserInvalidRole() {
        assertThrows(RuntimeException.class, () -> {
            userService.createUser("John", "Doe", "johndoe", "pass", "invalid");
        });
    }

    @Test
    void testCreateUserUsernameTaken() {
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            userService.createUser("John", "Doe", "taken", "password", "patient");
        });
    }

    @Test
    void testGetAllUsers() {
        userService.getAllUsers();
        verify(userRepository).findAll();
    }

    @Test
    void testGetUser() {
        Patient patient = new Patient(1, "John", "Doe", "johndoe", "pass");
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));

        User found = userService.getUser(1);

        assertNotNull(found);
        assertEquals(1, found.getId());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById(1)).thenReturn(true);
        boolean deleted = userService.deleteUser(1);
        assertTrue(deleted);
        verify(userRepository).deleteById(1);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(99)).thenReturn(false);
        boolean deleted = userService.deleteUser(99);
        assertFalse(deleted);
        verify(userRepository, never()).deleteById(99);
    }

    @Test
    void testUpdateUser() {
        Patient existing = new Patient(1, "Old", "Old", "old", "pass");
        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        com.HamesJoman.patient_portal.dto.UserRequest request = new com.HamesJoman.patient_portal.dto.UserRequest();
        request.setFirstName("New");
        request.setLastName("New");
        request.setUsername("new");
        request.setRole("Patient");
        request.setPassword("newpass");

        User updated = userService.updateUser(1, request);

        assertEquals("New", updated.getFirstName());
        assertEquals("new", updated.getUsername());
        assertNotEquals("pass", updated.getPassword());
    }

    @Test
    void testRecordLogin() {
        Patient patient = new Patient(1, "John", "Doe", "johndoe", "pass");
        when(userRepository.findById(1)).thenReturn(Optional.of(patient));

        userService.recordLogin(1);

        assertNotNull(patient.getLastLogin());
        verify(userRepository).save(patient);
    }

    @Test
    void testVerifyPassword() {
        // BCrypt is hard to mock correctly without mocking the static or bean
        // but since we are using the real encoder in the service, we can just test the logic
        String pass = "password123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(pass);

        assertTrue(userService.verifyPassword(pass, hashed));
        assertFalse(userService.verifyPassword("wrong", hashed));
    }

    @Test
    void testGetUserByUsername() {
        Patient patient = new Patient(1, "John", "Doe", "johndoe", "pass");
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(patient));

        User found = userService.getUserByUsername("johndoe");

        assertEquals(patient, found);
    }

    @Test
    void testGetUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertNull(userService.getUser(99));
    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertNull(userService.getUserByUsername("unknown"));
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertNull(userService.updateUser(99, new com.HamesJoman.patient_portal.dto.UserRequest()));
    }
}
