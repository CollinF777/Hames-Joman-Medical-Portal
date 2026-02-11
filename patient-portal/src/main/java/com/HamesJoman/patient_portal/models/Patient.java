package com.HamesJoman.patient_portal.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Model for Patient.
 *
 * @author Collin Fair
 */
@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends User {
    public Patient() {

    }

    public Patient(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        setRole("Patient");
    }
}