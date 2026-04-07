package com.HamesJoman.patientportalguiapp.logic;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LoginController response handling logic.
 * Tests the status code branching without needing JavaFX or mocks.
 *
 * This is most similar to our backend unit tests imo
 * We are isolating the logic components and testing them here
 * Now successfully mocking the API!
 * Must be run on Java 17 or else its not gonna work
 * This is why AI is useless
 * I got stuck in dependency hell until I decided to stop seeing if an AI could
 * figure out the dependency problem when I failed and instead I just started swapping
 * to every SDK I have on this machine
 *
 * This is also kind of just an API tester so idk if this is good enough or not but
 * I see no other way to do it since our controller methods are private
 *
 * @author Collin Fair
 */
class LoginControllerLogicTest {
    /**
     * Before every test make a new session so they dont mess with each other
     *
     * Tbh idk if this is needed anymore with us not testing SessionManager in this class
     * anymore but I'm scared to get rid of it
     */
    @BeforeEach
    void clearSession() {
        SessionManager.getInstance().clear();
    }

    /**
     * If the API returns a 401 then no login should be recorded in the session
     *
     * @throws Exception if the login fails
     */
    @Test
    void failedLoginNoSessionTest() throws Exception {
        /**
         * So this is how you actually end up mocking our api
         * We use MockedStatic to mock the entire class then from there we can
         * add in a fake response
         * I imagine if you have multiple API clients for some reason you would need to
         * do multiple static mocks but we dont have that so thats not my problem
         * I also dont know if this is the best way to mock APIs but it works so Im happy
         */
        try (MockedStatic<ApiClient> mockedApi = Mockito.mockStatic(ApiClient.class)) {
            /**
             * I just realized I should probably explain why to do mock on this but mockStatic
             * on the ApiClient, its probably self-explanatory given the name of the method but
             * every method in ApiClient is static and as such you need to use MockedStatic vs
             * HttpResponse is just a interface and isnt being called statically so we just need to
             * mock a fake object
             */
            HttpResponse<String> fakeResponse = mock(HttpResponse.class);
            // Mock a 401 response
            when(fakeResponse.statusCode()).thenReturn(401);

            // When the mocked ApiClient logins with any string then its gonna return our fake response
            mockedApi.when(() -> ApiClient.login(anyString(), anyString())).thenReturn(fakeResponse);

            HttpResponse<String> response = ApiClient.login("fjeijfibolwdj", "fjefnih");

            assertFalse(SessionManager.getInstance().isLoggedIn());
        }
    }

    /**
     * When the API returns a 200 it should successfully be logged into a session
     *
     * @throws Exception if the login fails
     */
    @Test
    void successfulLoginSetsSessionTest() throws Exception {
        try (MockedStatic<ApiClient> mockedApi = Mockito.mockStatic(ApiClient.class)) {
            HttpResponse<String> fakeResponse = mock(HttpResponse.class);
            when(fakeResponse.statusCode()).thenReturn(200);
            // Return a minimal valid JSON user, password isnt included because its gonna be hashed
            // and im just gonna push it through later
            when(fakeResponse.body()).thenReturn(
                    "{\"id\":1,\"firstName\":\"Alyssa\",\"lastName\":\"Ackman\"," +
                            "\"username\":\"aackm1\",\"role\":\"Admin\"}"
            );

            mockedApi.when(() -> ApiClient.login(anyString(), anyString()))
                    .thenReturn(fakeResponse);

            // Replicate the controller's 200 handling
            HttpResponse<String> response = ApiClient.login("aackm1", "password");
            if (response.statusCode() == 200) {
                // If you dont know what these do move around the frontend files until you find the big explanation
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.body());

                SessionManager.getInstance().setUser(json.get("id").asInt(), json.get("firstName").asText(),
                        json.get("lastName").asText(), json.get("username").asText(), json.get("role").asText());
            }

            assertTrue(SessionManager.getInstance().isLoggedIn());
            assertEquals("Admin", SessionManager.getInstance().getRole());
            assertEquals("aackm1", SessionManager.getInstance().getUsername());
            assertEquals("Alyssa Ackman", SessionManager.getInstance().getFullName());
        }
    }

    /**
     * When we get a 500 response then there shouldnt be a set session
     *
     * @throws Exception if the login fails
     */
    @Test
    void serverErrorNoSessionTest() throws Exception {
        try (MockedStatic<ApiClient> mockedApi = Mockito.mockStatic(ApiClient.class)) {
            HttpResponse<String> fakeResponse = mock(HttpResponse.class);
            when(fakeResponse.statusCode()).thenReturn(500);

            mockedApi.when(() -> ApiClient.login(anyString(), anyString())).thenReturn(fakeResponse);

            HttpResponse<String> response = ApiClient.login("idkbruhimagineimauser", "guesswhatimapw");

            assertFalse(SessionManager.getInstance().isLoggedIn());
        }
    }
}