package com.HamesJoman.patientportalguiapp.ui;

import com.HamesJoman.patientportalguiapp.ApiClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.MockedStatic;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Frontend tests for LoginController.
 * Tests UI behavior and input validation without needing a real backend.
 * ApiClient is mocked with Mockito so no server connection is required.
 *
 * This is going to be heavily commented so it can be used as a reference point
 *
 * @author Collin Fair
 */
@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

    /**
     * Loads the login view before each test.
     * @Start is TestFX's equivalent of @BeforeEach for JavaFX setup.
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/login-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Test that clicking login with both fields empty shows an error message.
     * No API call should be made in this case.
     */
    @Test
    void testEmptyFieldsShowsError(FxRobot robot) {
        // The robot serves as a way to click buttons to verify youre going to get the correct response
        robot.clickOn("#loginButton");

        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Test that clicking login with only username filled still shows an error.
     * The password field is blank so validation should catch it.
     */
    @Test
    void testOnlyUsernameFilledShowsError(FxRobot robot) {
        robot.clickOn("#usernameField").write("fakeUser");
        robot.clickOn("#loginButton");

        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Test that clicking login with only password filled still shows an error.
     */
    @Test
    void testOnlyPasswordFilledShowsError(FxRobot robot) {
        robot.clickOn("#passwordField").write("fakePassword");
        robot.clickOn("#loginButton");

        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Test that a 401 response from the API shows "Invalid Username or Password".
     * Mocks ApiClient.login() to return a 401 without hitting a real server.
     */
    @Test
    void testInvalidCredentialsShowsError(FxRobot robot) throws Exception {
        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(401);

        try (MockedStatic<ApiClient> mocked = mockStatic(ApiClient.class)) {
            mocked.when(() -> ApiClient.login(anyString(), anyString()))
                    .thenReturn(fakeResponse);

            robot.clickOn("#usernameField").write("LUser");
            robot.clickOn("#passwordField").write("LPassword");
            robot.clickOn("#loginButton");

            FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Invalid Username or Password"));
        }
    }

    /**
     * Test that a server error (500) shows the status code in the error message.
     */
    @Test
    void testServerErrorShowsStatusCode(FxRobot robot) throws Exception {
        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(500);

        try (MockedStatic<ApiClient> mocked = mockStatic(ApiClient.class)) {
            mocked.when(() -> ApiClient.login(anyString(), anyString()))
                    .thenReturn(fakeResponse);

            robot.clickOn("#usernameField").write("someuser");
            robot.clickOn("#passwordField").write("somepass");
            robot.clickOn("#loginButton");

            FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Error: 500"));
        }
    }

    /**
     * Test that if ApiClient throws an exception (server unreachable),
     * the UI shows a connection error message rather than crashing.
     */
    @Test
    void testConnectionFailureShowsError(FxRobot robot) {
        // Mocked the api client to check the error message when you cant connect to the server
        try (MockedStatic<ApiClient> mocked = mockStatic(ApiClient.class)) {
            mocked.when(() -> ApiClient.login(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Connection refused"));

            robot.clickOn("#usernameField").write("someuser");
            robot.clickOn("#passwordField").write("somepass");
            robot.clickOn("#loginButton");

            FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
        }
    }

    /**
     * Test that a successful admin login (200 with admin role JSON) navigates
     * away from the login screen. We verify the title changes to Admin Dashboard.
     */
    @Test
    void testSuccessfulAdminLoginNavigatesToDashboard(FxRobot robot) throws Exception {
        String adminJson = "{\"id\":1,\"firstName\":\"Test\",\"lastName\":\"Admin\"," +
                "\"username\":\"admin\",\"role\":\"Admin\"," +
                "\"lastLogin\":null,\"lastPasswordChange\":null}";

        // Set up the moked response to return a 200 status code and return the json
        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(200);
        when(fakeResponse.body()).thenReturn(adminJson);

        try (MockedStatic<ApiClient> mocked = mockStatic(ApiClient.class)) {
            mocked.when(() -> ApiClient.login(anyString(), anyString()))
                    .thenReturn(fakeResponse);

            robot.clickOn("#usernameField").write("admin");
            robot.clickOn("#passwordField").write("password");
            robot.clickOn("#loginButton");

            robot.sleep(300);

            Stage stage = (Stage) robot.lookup("#logoutButton").query().getScene().getWindow();
            assertEquals("Admin Dashboard", stage.getTitle());
        }
    }

    /**
     * Test that a successful patient login navigates to the Patient Dashboard.
     */
    @Test
    void testSuccessfulPatientLoginNavigatesToDashboard(FxRobot robot) throws Exception {
        String patientJson = "{\"id\":2,\"firstName\":\"John\",\"lastName\":\"Doe\"," +
                "\"username\":\"johndoe\",\"role\":\"Patient\"," +
                "\"lastLogin\":null,\"lastPasswordChange\":null}";

        HttpResponse<String> fakeResponse = mock(HttpResponse.class);
        when(fakeResponse.statusCode()).thenReturn(200);
        when(fakeResponse.body()).thenReturn(patientJson);

        HttpResponse<String> apptResponse = mock(HttpResponse.class);
        when(apptResponse.statusCode()).thenReturn(200);
        when(apptResponse.body()).thenReturn("[]");

        try (MockedStatic<ApiClient> mocked = mockStatic(ApiClient.class)) {
            mocked.when(() -> ApiClient.login(anyString(), anyString()))
                    .thenReturn(fakeResponse);
            mocked.when(() -> ApiClient.getAppointmentsByPatient(anyInt()))
                    .thenReturn(apptResponse);

            robot.clickOn("#usernameField").write("johndoe");
            robot.clickOn("#passwordField").write("password");
            robot.clickOn("#loginButton");

            robot.sleep(300);

            Stage stage = (Stage) robot.lookup("#logoutButton").query().getScene().getWindow();
            assertEquals("Patient Dashboard", stage.getTitle());
        }
    }
}