package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.dto.UserRequest;
import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.repositories.AppointmentRepository;
import com.HamesJoman.patient_portal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service layer for managing User entities.
 * Handles all business logic related to user operations including CRUD operations,
 * user authentication tracking, and maintaining an in-memory cache of users.
 *
 * This service uses an ArrayList for storage, meaning data persists only during
 * application runtime and is lost when the application stops.
 *
 * @author Collin Fair
 * (Note a lot of this code is from Matt in 427)
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // For password hashing
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Creates a new user with the specified details and role.
     * Hashes the password with BCrypt before storing
     * Automatically assigns a unique ID and sets the password change timestamp.
     * The role determines which subclass (Patient, Doctor, or Admin) is instantiated.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param username Unique username for login
     * @param password User's password (hashed)
     * @param role The user's role: "patient", "doctor", or "admin"
     * @return The newly created User object (Patient, Doctor, or Admin) with db assigned ID
     * @throws RuntimeException if the role is not valid
     */
    public User createUser(String firstName, String lastName, String username, String password, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken: " + username);
        }
        // Hash password
        String hashedPassword = passwordEncoder.encode(password);

        // Create the appropriate user subclass based on role
        User newUser;
        switch (role.toLowerCase()) {
            // Important note: id is getting set as 0 because its gonna get ignored by jpa when a new entity is created
            case "patient":
                newUser = new Patient(0, firstName, lastName, username, hashedPassword);
                break;
            case "doctor":
                newUser = new Doctor(0, firstName, lastName, username, hashedPassword);
                break;
            case "admin":
                newUser = new Admin(0, firstName, lastName, username, hashedPassword);
                break;
            default:
                throw new RuntimeException("Invalid role for user: " + role);
        }
        return userRepository.save(newUser);
    }

    /**
     * Retrieves all users in the system from the db.
     * Returns every type in one list (Admin, Doctor, Patient)
     * Also this is new code added by me (Collin) i'm running off the assumption
     * admins will have every user displayed at once like in web app
     *
     * @return A list of all users in the db
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds and retrieves a user by their unique ID.
     *
     * @param id The unique identifier of the user to find
     * @return The User object if found, null if not found
     */
    public User getUser(int id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     *  Updates an existing user from a UserRequest
     *  If password is blank, the existing hashed password is kept unchanged
     *
     * @param id users id
     * @param request A request filled with user info
     * @return Updated user
     */
    public User updateUser(int id, UserRequest request) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setUsername(request.getUsername());
        existing.setRole(request.getRole());

        // Only update password if a new one was actually provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(existing);
    }

    /**
     * Deletes a user from the db by their ID.
     *
     * Fixed so now any active appointments that the user has now get cancelled
     * upon deletion of user which is what was causing errors before
     * If an appointment is already finished or cancelled then its left untouched
     *
     * We add transactional because if either deleting the user or cancelling
     * the appointment fails then both are rolled back otherwise we could get a
     * user that failed to delete but all appointments for them are canceled
     *
     * @param id The ID of the user to delete
     * @return true if a user was deleted, false if no user with that ID exists
     */
    @Transactional
    public boolean deleteUser(int id) {
       if (!userRepository.existsById(id)) {
           return false;
       }

        /**
         * Gathers every active appointment for a user
         *
         * We use Stream.concat as this basically acts like a loop to pull
         * every appointment regardless of their role i.e. if they're a patient or doctor
         *
         * S/O Brian Keenan for teaching us the wonders of stream (just a more complicated
         * for loop)
         */
        List<Appointment> toCancel = Stream.concat(
                appointmentRepository.findByPatient_Id(id).stream(),
                appointmentRepository.findByDoctor_Id(id).stream()
        ).filter(apt -> apt.getStatus() == Status.ACTIVE).collect(Collectors.toList());

        // Cancel all appointments
        if (!toCancel.isEmpty()) {
            toCancel.forEach(apt -> apt.setStatus(Status.CANCELLED));
            appointmentRepository.saveAll(toCancel);
            // Flush any cancellations now to prevent DB giving errors about updating with a non existent user
            appointmentRepository.flush();
        }

        userRepository.deleteById(id);
        return true;
    }

    /**
     * Records a login timestamp for a user.
     * Updates the user's lastLogin field to the current date and time.
     * This is new code not from 427 but I lowkey forgot about timestamping
     * users in 427 so that's my bad
     *
     * @param id The ID of the user who is logging in
     */
    public void recordLogin(int id) {
        // Find the user and update their last login time
        User user = getUser(id);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    /**
     * Verifies that a hashed password matches the regular password
     * We'll use this for login authentication
     *
     * @param rawPassword The plain password
     * @param hashedPassword Guess
     * @return true if they match, false otherwise
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}