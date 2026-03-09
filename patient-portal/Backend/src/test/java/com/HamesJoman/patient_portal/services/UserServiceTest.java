package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.repositories.AppointmentRepository;
import com.HamesJoman.patient_portal.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 *
 * Make sure you are using Mockito to mock the repos so we arent using the actual
 * db or any spring context is required
 *
 * @author Collin Fair
 */
@ExtendWith(MockitoExtension.class) // This is how you activate mockito
public class UserServiceTest {
    /**
     * We are creating fake objects so we arent messing up the real db
     * You should be using mockito and mock in basically every unit test
     */
    @Mock
    private UserRepository userRepository;

    // This is just for delete user
    @Mock
    private AppointmentRepository appointmentRepository;

    /**
     * InjectMocks is sorta self-explanatory based off the name but
     * It creates a real instance of UserService for testing and INJECTS (do I sound like mitra)
     * our fake objects into the constructor so basically we are actually using
     * the service layer without touching the db
     */
    @InjectMocks
    private UserService userService;

    private Patient samplePatient;
    private Doctor sampleDoctor;

    /**
     * Runs this before every single test
     * This makes a fresh user each test so they dont mess with each other
     */
    @BeforeEach
    void setUp() {
        samplePatient = mock(Patient.class);
        sampleDoctor = mock(Doctor.class);
    }

    /**
     * We are getting every user and are expecting to find our mocked user
     */
    @Test
    void getAllUsersTest() {
        // We are telling our fake repo what to return on a findAll() call
        when(userRepository.findAll()).thenReturn(List.of(samplePatient, sampleDoctor));

        // Here we actually call the service
        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    /**
     * Test for getting user by their id and they exist
     */
    @Test
    void getUserByIdExistsTest() {
        // Say what sample patients name should be
        when(samplePatient.getFirstName()).thenReturn("TestFirstName");
        // Should return our sample user on findById(1) I think im gonna stop commenting now
        when(userRepository.findById(1)).thenReturn(Optional.of(samplePatient));

        User result = userService.getUser(1);

        assertNotNull(result);
        assertEquals("TestFirstName", result.getFirstName());
    }

    /**
     * Test for getting user by their id and they dont exist
     */
    @Test
    void getUserByIdDoesNotExistTest() {
        when(userRepository.findById(777)).thenReturn(Optional.empty());

        User result = userService.getUser(777);

        assertNull(result);
    }

    /**
     * Test for creating a valid patient
     */
    @Test
    void createUserValidPatientTest() {
        when(samplePatient.getFirstName()).thenReturn("TestFirstName");
        when(userRepository.existsByUsername("TestUsername")).thenReturn(false);
        when(userRepository.save(any(Patient.class))).thenReturn(samplePatient);

        User result = userService.createUser("TestFirstName", "TestLastName",
                "TestUsername", "TestPassword", "patient");

        assertNotNull(result);
        assertEquals("TestFirstName", result.getFirstName());
    }

    /**
     * Test for creating a valid doctor
     */
    @Test
    void createUserValidDoctorTest() {
        when(sampleDoctor.getFirstName()).thenReturn("TestFirstName");
        when(userRepository.existsByUsername("TestUsername")).thenReturn(false);
        when(userRepository.save(any(Doctor.class))).thenReturn(sampleDoctor);

        User result = userService.createUser("TestFirstName", "TestLastName",
                "TestUsername", "TestPassword", "doctor");

        assertNotNull(result);
        assertEquals("TestFirstName", result.getFirstName());
    }

    /**
     * Test for creating a user with a duplicate username
     */
    @Test
    void createDuplicateUserTest() {
        when(userRepository.existsByUsername("TestUsername")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> userService.createUser("t", "t",
                        "TestUsername", "p", "patient"));
    }

    /**
     * Test for creating a user with a fake role
     */
    @Test
    void createUserInvalidRoleTest() {
        when(userRepository.existsByUsername("TestUserName")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.createUser("t", "t",
                        "TestUsername", "p", "AHHHHHHHHHH"));
    }

    /**
     * Test for deleting a user that has appointments
     */
    @Test
    void deleteUserWithAppointmentsTest() {
        Appointment appt = new Appointment();
        appt.setId(1);
        appt.setPatient(samplePatient);
        appt.setDoctor(sampleDoctor);
        appt.setStatus(Status.ACTIVE);

        when(userRepository.existsById(1)).thenReturn(true);
        when(appointmentRepository.findByPatient_Id(1)).thenReturn(List.of(appt));
        when(appointmentRepository.findByDoctor_Id(1)).thenReturn(List.of());

        boolean result = userService.deleteUser(1);

        assertTrue(result);
        assertEquals(Status.CANCELLED, appt.getStatus());

        // Verify user was deleted
        verify(userRepository, times(1)).deleteById(1);
    }

    /**
     * Test for deleting a user that has no appointments
     */
    @Test
    void deleteUserWithoutAppointmentsTest() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(appointmentRepository.findByPatient_Id(1)).thenReturn(List.of());
        when(appointmentRepository.findByDoctor_Id(1)).thenReturn(List.of());

        boolean result = userService.deleteUser(1);

        assertTrue(result);
        verify(userRepository, times(1)).deleteById(1);
    }

    /**
     * Test for deleting a user that doesnt exist
     */
    @Test
    void deleteNonexistingUserTest() {
        when(userRepository.existsById(777)).thenReturn(false);

        boolean result = userService.deleteUser(777);

        assertFalse(result);
        verify(userRepository, never()).deleteById(anyInt());
    }
}
