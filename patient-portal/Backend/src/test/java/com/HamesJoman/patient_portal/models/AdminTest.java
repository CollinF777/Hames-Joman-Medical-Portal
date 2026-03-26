package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Admin model
 * Making sure the constructors and getters work as expected
 * and that role defaults to "Admin" when using the full constructor
 *
 * @author Mohamed Musa & Ali Beheshti
 */
class AdminTest {

    /**
     * Test that the full constructor sets all fields correctly
     * Also checks that role defaults to "Admin"
     */
    @Test
    void testAdminConstructorAndGetters() {
        Admin admin = new Admin(1, "Lisa", "Cuddy", "lcuddy", "hospital");
        
        assertEquals(1, admin.getId());
        assertEquals("Lisa", admin.getFirstName());
        assertEquals("Cuddy", admin.getLastName());
        assertEquals("lcuddy", admin.getUsername());
        assertEquals("hospital", admin.getPassword());
        assertEquals("Admin", admin.getRole());
    }

    /**
     * Test that the default no-arg constructor creates a non-null Admin object
     */
    @Test
    void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNotNull(admin);
    }
}
