package com.HamesJoman.patient_portal.controllers;

import com.HamesJoman.patient_portal.dto.UserRequest;
import com.HamesJoman.patient_portal.models.*;
import com.HamesJoman.patient_portal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing User entities via HTTP endpoints.
 * Provides RESTful API endpoints for creating, reading, updating, and deleting users,
 * as well as managing user roles and tracking login activity.
 *
 * All endpoints are prefixed with /api/users
 *
 * @author Collin Fair
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Service layer dependency for user business logic.
     * Automatically injected by Spring's dependency injection.
     */
    @Autowired
    private UserService userService;

    /**
     * GET /api/users
     * Retrieves all users in the system.
     *
     * @return List of all User objects in the system
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * GET /api/users/{id}
     * Retrieves a specific user by their unique ID.
     *
     * Example: GET /api/users/1
     *
     * @param id The unique identifier of the user to retrieve
     * @return ResponseEntity with the User object if found (200 OK),
     *         or 404 NOT FOUND if user doesn't exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getUser(id);

        // Check if user exists
        if (user != null) {
            return ResponseEntity.ok(user);  // Return 200 OK with user data
        } else {
            return ResponseEntity.notFound().build();  // Return 404 NOT FOUND
        }
    }

    /**
     * POST /api/users
     * Creates a new user with the specified details.
     * The role determines whether a Patient, Doctor, or Admin is created.
     *
     *
     * @param request UserRequest DTO containing user details (firstName, lastName, username, password, role)
     * @return The newly created User object with assigned ID
     * @throws IllegalArgumentException if role is invalid (not patient/doctor/admin)
     */
    @PostMapping
    public User createUser(@RequestBody UserRequest request) {
        return userService.createUser(
                request.getFirstName(),
                request.getLastName(),
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );
    }

    /**
     * DELETE /api/users/{id}
     * Deletes a user based off their id
     *
     * @param id the unique identifier of the user
     * @return True if successful, false if not
     */
    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

}