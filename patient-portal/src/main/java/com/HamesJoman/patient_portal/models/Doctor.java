package com.HamesJoman.patient_portal.models;

/**
 * Model for Doctor.
 *
 * @author Collin Fair
 */
public class Doctor extends User {
    public Doctor(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        setRole("Doctor");
    }
}