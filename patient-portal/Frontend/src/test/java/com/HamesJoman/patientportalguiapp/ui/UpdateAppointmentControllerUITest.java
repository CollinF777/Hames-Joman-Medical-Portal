package com.HamesJoman.patientportalguiapp.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

/**
 * Frontend tests for UpdateAppointmentController.
 * Tests UI behavior on the Update Appointment popup, specifically verifying
 * that all edit fields are correctly disabled on load and that the connection
 * error is shown when no backend is running.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class UpdateAppointmentControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the Update Appointment popup as the screen.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/update-appt-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 420, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that the update button is disabled on load. The controller calls
     * setEditFieldsDisabled(true) during initialize(), so the user
     * shouldn't be able to submit an update until they've actually selected an
     * appointment from the select combo box first.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void updateButtonDisabledInitiallyTest(FxRobot robot) {
        // Nothing should be interactable until an appointment is selected from the combo box
        FxAssert.verifyThat("#updateButton", NodeMatchers.isDisabled());
    }

    /**
     * Verify that the appointment date picker is disabled on load. Just like the update
     * button, it should be locked out until the user picks an appointment to edit —
     * letting someone change the date before selecting a record doesn't make any sense.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void datePickerDisabledInitiallyTest(FxRobot robot) {
        // The date picker is part of the edit fields block that gets disabled in initialize()
        FxAssert.verifyThat("#appointmentDatePicker", NodeMatchers.isDisabled());
    }

    /**
     * Verify that when no backend is running, loadAppointments() catches the
     * connection exception and sets actionText to the appropriate error message.
     * This exercises the catch block that fires during initialize() when
     * ApiClient.getAllAppointments() can't reach the server.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void serverConnectionFailsOnLoadTest(FxRobot robot) {
        // No backend running, so ApiClient.getAllAppointments() will throw a ConnectException
        // and the controller should catch it and show this message on the action label
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
    }
}
