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
 * Because {@code initialize()} reads from {@link SessionManager} the moment the
 * FXML is loaded, the session must be populated before the loader is called —
 * that's why {@code setUser} lives inside {@code @Start} rather than a
 * {@code @BeforeEach}. {@code @BeforeEach} runs before {@code @Start} in the
 * TestFX lifecycle, so anything set there would be overwritten or missed entirely
 * if the FX thread fires first.
 *
 * This is JUST for ui testing, logic should be in the logic folder.
 * Separated so that logic tests can be run without spinning up JavaFX every time —
 * keeps the feedback loop fast when you only changed something in the backend layer.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class DoctorControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the doctor dashboard as the default screen.
     *
     * The session is populated HERE, before the FXML loader fires, because
     * {@code DoctorController.initialize()} calls {@code SessionManager.getInstance().getFullName()}
     * immediately on load. If we set the user any later we'd be too late — the welcome label
     * would already have been written with whatever garbage was in the session at that point.
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
     * Verify that {@code nextApptLabel} stays at its FXML default of "Loading..." when
     * the backend is unreachable.
     *
     * The controller only updates this label on a successful 200 response from the API.
     * Since there's no backend running, the catch block fires instead, and that block
     * never touches {@code nextApptLabel} — so it should still read exactly what the
     * FXML set it to.
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
     * Verify that {@code totalApptsLabel} stays at its FXML default of "0 Total Appointments"
     * when the backend is unreachable.
     *
     * Same reasoning as {@code nextApptLabelRemainsAtLoadingOnNoServerTest} — the label is
     * only updated on a successful API response, and the catch block doesn't touch it.
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
