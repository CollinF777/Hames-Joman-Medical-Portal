package com.HamesJoman.patient_portal.services;

import com.HamesJoman.patient_portal.models.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private ArrayList<User> users;
    private int idCounter = 1;

    public UserService() {
        users = new ArrayList<>();
    }

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
     * @return The newly created User object (Patient, Doctor, or Admin)
     * @throws RuntimeException if the role is not valid
     */
    public User createUser(String firstName, String lastName, String username, String password, String role) {
        // Generate unique ID for the new user
        int userId = idCounter++;

        // Create the appropriate user subclass based on role
        User newUser;
        switch (role.toLowerCase()) {
            case "patient":
                newUser = new Patient(userId, firstName, lastName, username, password);
                break;
            case "doctor":
                newUser = new Doctor(userId, firstName, lastName, username, password);
                break;
            case "admin":
                newUser = new Admin(userId, firstName, lastName, username, password);
                break;
            default:
                throw new RuntimeException("Invalid role for user: " + role);
        }
        users.add(newUser);
        return newUser;
    }

    /**
     * Retrieves all users in the system.
     * Returns a copy of the users list so the main one doesnt get messed up
     * Also this is new code added by me (Collin) i'm running off the assumption
     * admins wiil have every user displayed at once like in web app
     *
     * @return A new ArrayList containing all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Finds and retrieves a user by their unique ID.
     * Iterates through the users list to find a matching ID.
     *
     * @param id The unique identifier of the user to find
     * @return The User object if found, null if not found
     */
    public User getUser(int id) {
        // Loop through all users to find matching ID
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                return users.get(i);
            }
        }
        // Return null if no user found with that ID
        return null;
    }

    /**
     * Updates an existing user's information.
     * Searches for the user by ID and replaces their data with the updated user object.
     * The ID of the updated user is preserved to maintain referential integrity.
     *
     * @param id The ID of the user to update
     * @param updatedUser The User object containing updated information
     * @return The updated User object if found, null if user doesn't exist
     */
    public User updateUser(int id, User updatedUser) {
        // Search through the users list to find the matching ID
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                // Preserve the original ID to prevent ID changes
                updatedUser.setId(id);
                // Replace the old user object with the updated one
                users.set(i, updatedUser);
                return updatedUser;
            }
        }
        // Return null if user not found
        return null;
    }

    /**
     * Deletes a user from the system by their ID.
     * Searches through the list and removes the user if found.
     *
     * @param id The ID of the user to delete
     * @return true if a user was deleted, false if no user with that ID exists
     */
    public boolean deleteUser(int id) {
        // Loop through users to find the one to delete
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                users.remove(i);
                return true;
            }
        }
        // Return false if user not found
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
        }
    }
}