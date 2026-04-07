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
 * Frontend tests for PatientController.
 * Tests UI behavior for the patient dashboard view, specifically verifying that
 * the welcome label is personalized correctly, and that labels which depend on
 * a live server response stay at their FXML defaults when no backend is reachable.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class PatientControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the patient dashboard as the default screen.
     * The session is set here — before the loader runs — so that initialize() picks up
     * the correct user data the moment the controller is instantiated by the FXMLLoader.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        // Seed the session first — initialize() reads from SessionManager immediately,
        // so this MUST happen before loader.load() is called
        SessionManager.getInstance().setUser(3, "Sarah", "Mitchell", "smitch3", "Patient");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/patient-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 701, 615);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Clear the session after each test so that leftover session state doesn't bleed into
     * subsequent tests. Without this, a test that runs after this class could find a
     * stale "Patient" user still sitting in the singleton.
     *
     * @throws Exception if clearing the session somehow throws (shouldn't happen, but JUnit
     *                   expects the signature to allow it)
     */
    @AfterEach
    void clearSession() throws Exception {
        SessionManager.getInstance().clear();
    }

    /**
     * Verify that the welcome label is populated with the patient's full name on load.
     * Patients do NOT get a "Dr." prefix — that's doctor-only — so the expected string
     * is just "Welcome back, Sarah Mitchell!".
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void welcomeLabelShowsPatientNameTest(FxRobot robot) {
        // No "Dr." prefix here — patients get a plain first + last name greeting
        FxAssert.verifyThat("#welcomeLabel", LabeledMatchers.hasText("Welcome back, Sarah Mitchell!"));
    }

    /**
     * Verify that nextApptLabel stays at "Loading..." when no server is running.
     * The controller only updates this label on a successful 200 response from the API;
     * the catch block doesn't touch it, so the FXML default survives intact.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void nextApptLabelRemainsAtLoadingOnNoServerTest(FxRobot robot) {
        // No backend means the ApiClient call throws, lands in the catch block,
        // and nextApptLabel is never reassigned — so "Loading..." is exactly right
        FxAssert.verifyThat("#nextApptLabel", LabeledMatchers.hasText("Loading..."));
    }

    /**
     * Verify that totalApptsLabel stays at its FXML default of "0 Total Appointments"
     * when no server is running. Like nextApptLabel, it is only updated on a 200
     * response, so the catch path leaves the default value untouched.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void totalApptsLabelRemainsAtDefaultOnNoServerTest(FxRobot robot) {
        // Same story as nextApptLabel — the catch block only populates the list view,
        // so the totals label never gets a real value without a live server
        FxAssert.verifyThat("#totalApptsLabel", LabeledMatchers.hasText("0 Total Appointments"));
    }
}
