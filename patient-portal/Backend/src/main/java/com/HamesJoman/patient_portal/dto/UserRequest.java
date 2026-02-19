package com.HamesJoman.patient_portal.dto;

/**
 * Data Transfer Object for user creation.
 * Contains all user fields that can be set via the API.
 *
 * I'm gonna bsfr idk if this is how you're meant to do DTO's but this is how im doing it
 *
 * DTO's are meant to keep data safe like you arent exposing any internal database objects when going between
 * API and client
 * Jenny if you see this during a epic please specify
 */
public class UserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;

    // Getters and setters required for JSON serialization/deserialization
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
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
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}