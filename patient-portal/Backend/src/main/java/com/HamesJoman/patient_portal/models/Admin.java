package com.HamesJoman.patient_portal.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Model for Admin.
 *
 * @author Collin Fair
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public Admin() {

    }

    public Admin(int id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password);
        setRole("Admin");
    }
}