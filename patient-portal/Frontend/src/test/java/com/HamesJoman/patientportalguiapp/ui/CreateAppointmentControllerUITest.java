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
import org.testfx.matcher.control.LabeledMatchers;

/**
 * Frontend tests for CreateAppointmentController.
 * Tests UI behavior when the backend is unreachable and when the form is submitted incomplete.
 *
 * Like the other UI tests, we are not spinning up the backend here so anything
 * that makes an API call is going to fail, which is actually what we want to verify
 * The controller should handle that gracefully and show the right message to the user
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class CreateAppointmentControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the Create Appointment view loaded.
     * The spinners and time setup all happen inside initialize() so by the time any
     * test runs, the controller has already done its thing.
     *
     * @param stage the main JavaFX stage
     * @throws Exception if the FXML fails to load (hope this never happens)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/create-appt-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 420);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that when the controller initializes and the backend is not running,
     * the actionText label correctly tells the user the server could not be reached.
     *
     * This fires during loadUsers() which is called from initialize(), so no user
     * interaction is needed — just check the label right away.
     */
    @Test
    void serverConnectionFailsOnLoadTest() {
        // No backend running, so loadUsers() throws a ConnectException
        // and the controller catches it and sets this message
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
    }

    /**
     * Verify that clicking "Create Appointment" with nothing filled in shows the
     * correct validation message.
     *
     * The date picker is null by default and both combo boxes are empty since the
     * backend never responded, so the controller should catch all of that and
     * tell the user to fill everything in before submitting.
     *
     * @param robot TestFX testing robot that clicks the button for us
     */
    @Test
    void missingSelectionShowsErrorTest(FxRobot robot) {
        // The robot clicks the button by its visible text since there's no fx:id on it
        robot.clickOn("Create Appointment");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please fill in all fields."));
    }
}
