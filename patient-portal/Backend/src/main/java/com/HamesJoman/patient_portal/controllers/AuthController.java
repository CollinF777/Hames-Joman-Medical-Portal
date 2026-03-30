package com.HamesJoman.patient_portal.controllers;

import com.HamesJoman.patient_portal.dto.LoginRequest;
import com.HamesJoman.patient_portal.models.User;
import com.HamesJoman.patient_portal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for authentication (login) endpoints
 * Handles login requests
 *
 * Endpoints start with /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    /**
     * POST /api/auth/login
     * Method to handle login, checks that user exists and has correct password then records login
     *
     * @param request A login request with all the necessary details for each login
     * @return Status based off login success
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        User user = userService.getUserByUsername(request.getUsername());

        // Dont allow users to get in if they dont exist
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // Dont allow users with wrong password in
        if (!userService.verifyPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        userService.recordLogin(user.getId());

        return ResponseEntity.ok(user);
    }
}
