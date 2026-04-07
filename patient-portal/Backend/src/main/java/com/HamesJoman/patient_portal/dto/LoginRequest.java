package com.HamesJoman.patient_portal.dto;

/**
 * DTO for login reqs
 * Only has fields needed for auth
 *
 * @author Collin Fair
 */
public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
