package com.HamesJoman.patient_portal.models;

/**
 * Model for Admin.
 *
 * @author Collin Fair
 */
public class Admin extends User {
    public Admin(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        setRole("Admin");
    }
}