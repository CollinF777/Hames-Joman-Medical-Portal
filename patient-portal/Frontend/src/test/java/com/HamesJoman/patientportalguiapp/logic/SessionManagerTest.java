package com.HamesJoman.patientportalguiapp.logic;

import com.HamesJoman.patientportalguiapp.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class to make sure the session manager is working properly
 *
 * This was previously in LogicControllerTest but I have isolated it as its techinically
 * testing a different class
 *
 * @author Collin Fair
 */
public class SessionManagerTest {
    /**
     * Before every test make a new session so they dont mess with each other
     */
    @BeforeEach
    void clearSession() {
        SessionManager.getInstance().clear();
    }

    /**
     * If a user cannot login, then no session should be made
     */
    @Test
    void sessionNotSetOnBadCredentialsTest() {
        // If login fails, session should remain empty which would be false
        assertFalse(SessionManager.getInstance().isLoggedIn());
    }

    /**
     * Verify that if a user logins properly then a session should be made with their info
     */
    @Test
    void sessionSetAfterValidLoginTest() {
        SessionManager.getInstance().setUser(1, "Stanley", "Valdez", "GMoney527", "Doctor");
        assertTrue(SessionManager.getInstance().isLoggedIn());
        assertEquals("Doctor", SessionManager.getInstance().getRole());
        assertEquals("GMoney527", SessionManager.getInstance().getUsername());
    }

    /**
     * After a user logouts, there should be no left over data in the session
     */
    @Test
    void sessionClearedOnLogoutTest() {
        SessionManager.getInstance().setUser(1, "Ryan", "Smellagrossi", "rfant1", "Patient");
        SessionManager.getInstance().clear();
        assertFalse(SessionManager.getInstance().isLoggedIn());
        assertNull(SessionManager.getInstance().getUsername());
    }

    /**
     * Test that the session meshes the name together properly
     */
    @Test
    void getFullNameTest() {
        SessionManager.getInstance().setUser(1, "Collin", "Fair", "cfair4", "Admin");
        assertEquals("Collin Fair", SessionManager.getInstance().getFullName());
    }

    /**
     * A freshly cleared session should have userId of 0 and isLoggedIn false
     */
    @Test
    void clearedSessionHasDefaultValuesTest() {
        SessionManager.getInstance().setUser(5, "Test", "User", "tuser", "Patient");
        SessionManager.getInstance().clear();

        assertFalse(SessionManager.getInstance().isLoggedIn());
        assertEquals(0, SessionManager.getInstance().getUserId());
        assertNull(SessionManager.getInstance().getFirstName());
        assertNull(SessionManager.getInstance().getLastName());
        assertNull(SessionManager.getInstance().getRole());
    }
}
