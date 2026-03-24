package com.HamesJoman.patient_portal.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

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

    @Test
    void testDefaultConstructor() {
        Admin admin = new Admin();
        assertNotNull(admin);
    }
}
