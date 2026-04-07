package com.HamesJoman.patientportalguiapp.ui;

import com.HamesJoman.patientportalguiapp.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

/**
 * Frontend tests for DoctorController.
 * Tests UI behavior for the doctor dashboard view, specifically verifying that
 * labels are initialized correctly when the controller's {@code initialize()} runs
 * with an active session but no reachable backend.
 *
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class DoctorControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the doctor dashboard as the default screen.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        // Seed the session before the loader triggers initialize() — order matters here
        SessionManager.getInstance().setUser(2, "Marcus", "Dumont", "mdumont1", "Doctor");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/doctor-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 701, 615);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Clear the session after each test so that session state doesn't leak into
     * subsequent tests. Without this, a test that runs after this class could pick up
     * Marcus Dumont's session and produce a false positive or a confusing failure.
     */
    @AfterEach
    void clearSession() {
        SessionManager.getInstance().clear();
    }

    /**
     * Verify that the welcome label is correctly composed from the session's full name
     * and includes the "Dr." prefix expected for doctor-role users.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void welcomeLabelShowsDoctorNameTest(FxRobot robot) {
        // The controller builds "Welcome back, Dr. " + getFullName() + "!" in initialize(),
        // so as long as the session was set before the loader ran this should match exactly
        FxAssert.verifyThat("#welcomeLabel", LabeledMatchers.hasText("Welcome back, Dr. Marcus Dumont!"));
    }

    /**
     * Verify that nextApptLabel stays at its FXML default of "Loading..." when
     * the backend is unreachable.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void nextApptLabelRemainsAtLoadingOnNoServerTest(FxRobot robot) {
        // No backend running — the API call throws, the catch block runs, and nextApptLabel
        // is never written to, so we expect it to still be at the FXML default value
        FxAssert.verifyThat("#nextApptLabel", LabeledMatchers.hasText("Loading..."));
    }

    /**
     * Verify that totalApptsLabel stays at its FXML default of "0 Total Appointments"
     * when the backend is unreachable.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void totalApptsLabelRemainsAtDefaultOnNoServerTest(FxRobot robot) {
        // The catch block only adds "Couldn't connect to server" to the appointmentListView —
        // totalApptsLabel is left alone entirely, so the FXML default should still be intact
        FxAssert.verifyThat("#totalApptsLabel", LabeledMatchers.hasText("0 Total Appointments"));
    }
}
