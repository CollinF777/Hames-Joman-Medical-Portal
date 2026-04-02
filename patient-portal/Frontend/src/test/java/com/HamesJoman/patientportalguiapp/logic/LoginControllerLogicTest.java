package com.HamesJoman.patientportalguiapp.logic;

import com.HamesJoman.patientportalguiapp.SessionManager;
import com.HamesJoman.patientportalguiapp.controllers.LoginController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginController response handling logic.
 * Tests the status code branching without needing JavaFX or mocks.
 *
 * This is most similar to our backend unit tests imo
 * Not perfect, this does not mock the API or reactions to specific responses
 * However mocking the api/server and trying to trigger certain status codes was
 * what was causing the errors before so Im not sure TestFX, JUnit, or Mockito supports that
 *
 * @author Collin Fair
 */
class LoginControllerLogicTest {
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
    void testSessionNotSetOnBadCredentials() {
        // If login fails, session should remain empty which would be false
        assertFalse(SessionManager.getInstance().isLoggedIn());
    }

    /**
     * Verify that if a user logins properly then a session should be made with their info
     */
    @Test
    void testSessionSetAfterValidLogin() {
        SessionManager.getInstance().setUser(1, "Stanley", "Valdez", "GMoney527", "Doctor");
        assertTrue(SessionManager.getInstance().isLoggedIn());
        assertEquals("Doctor", SessionManager.getInstance().getRole());
        assertEquals("GMoney527", SessionManager.getInstance().getUsername());
    }

    /**
     * After a user logouts, there should be no left over data in the session
     */
    @Test
    void testSessionClearedOnLogout() {
        SessionManager.getInstance().setUser(1, "Ryan", "Smellagrossi", "rfant1", "Patient");
        SessionManager.getInstance().clear();
        assertFalse(SessionManager.getInstance().isLoggedIn());
        assertNull(SessionManager.getInstance().getUsername());
    }

    /**
     * Test that the session meshes the name together properly
     */
    @Test
    void testGetFullName() {
        SessionManager.getInstance().setUser(1, "Collin", "Fair", "cfair4", "Admin");
        assertEquals("Collin Fair", SessionManager.getInstance().getFullName());
    }
}