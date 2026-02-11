package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Creates a new user with the specified details and role.
     * Automatically assigns a unique ID and sets the password change timestamp.
     * The role determines which subclass (Patient, Doctor, or Admin) is instantiated.
     *
     * @param firstName User's first name
     * @param lastName User's last name
     * @param username Unique username for login
     * @param password User's password (we'll figure out how to hash at some point... not today though)
     * @param role The user's role: "patient", "doctor", or "admin"
     * @return The newly created User object (Patient, Doctor, or Admin) with db assigned ID
     * @throws RuntimeException if the role is not valid
     */
    public User createUser(String firstName, String lastName, String username, String password, String role) {
        // Create the appropriate user subclass based on role
        User newUser;
        switch (role.toLowerCase()) {
            // Important noteL id is getting set as 0 because its gonna get ignored by jpa when a new entity is created
            case "patient":
                newUser = new Patient(0, firstName, lastName, username, password);
                break;
            case "doctor":
                newUser = new Doctor(0, firstName, lastName, username, password);
                break;
            case "admin":
                newUser = new Admin(0, firstName, lastName, username, password);
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
     * Updates an existing user's information.
     * ID is preserved to maintain integrity
     * If the db cant find the id then it returns null
     *
     * This is a complete update, we probably wanna make a partial
     * update in the future so nothing is overwritten on accident
     * but that's a future problem
     *
     * @param id The ID of the user to update
     * @param updatedUser The User object containing updated information
     * @return The updated User object if found, null if user doesn't exist
     */
    public User updateUser(int id, User updatedUser) {
        if (userRepository.existsById(id)) {
            updatedUser.setId(id);
            return userRepository.save(updatedUser);
        }
        return null;
    }

    /**
     * Deletes a user from the db by their ID.
     *
     * If like web app, we wanna make this not a full delete like it currently is
     * but instead make the user inactive, or maybe that was just appointments
     * if you read this then remind me to ask Jenny
     *
     * @param id The ID of the user to delete
     * @return true if a user was deleted, false if no user with that ID exists
     */
    public boolean deleteUser(int id) {
       if (userRepository.existsById(id)) {
           userRepository.deleteById(id);
           return true;
       }
       return false;
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
}