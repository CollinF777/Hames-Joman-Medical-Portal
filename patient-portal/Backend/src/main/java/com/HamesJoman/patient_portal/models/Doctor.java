package com.HamesJoman.patient_portal.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Model for Doctor.
 *
 * @author Collin Fair
 */
@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {
    public Doctor() {

    }

    public Doctor(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        setRole("Doctor");
    }
}