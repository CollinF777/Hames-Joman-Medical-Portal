package com.HamesJoman.patient_portal.models;

/**
 * Model for Patient.
 *
 * @author Collin Fair
 */
public class Patient extends User {
    public Patient(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        setRole("Patient");
    }
}