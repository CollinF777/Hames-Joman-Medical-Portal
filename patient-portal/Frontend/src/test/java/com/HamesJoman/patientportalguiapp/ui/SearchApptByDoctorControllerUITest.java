package com.HamesJoman.patientportalguiapp.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Frontend tests for SearchApptByDoctorController.
 * Tests UI behavior on the Search Appointments by Doctor view, specifically
 * verifying that the output box correctly reflects a connection failure on load
 * and that the doctor combo box stays empty when no backend is reachable.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class SearchApptByDoctorControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the Search Appointments by Doctor view
     * as the screen.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/search-appt-by-doctor-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that when no backend is running, loadDoctors() catches the connection
     * exception during initialize() and writes the appropriate error message into
     * outputBox. The user should never be left staring at a blank screen.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void serverConnectionFailsOnLoadTest(FxRobot robot) {
        // No backend running, so ApiClient.getAllUsers() will throw a ConnectException
        // and the controller should catch it and dump this into the output text area
        FxAssert.verifyThat("#outputBox", TextInputControlMatchers.hasText("Couldn't connect to server"));
    }

    /**
     * Verify that the doctor select combo box is completely empty when no backend is
     * available. Since loadDoctors() failed to fetch any users, there should be
     * nothing to populate the list with — zero items, not even a stale placeholder.
     *
     * @param robot TestFX testing robot used to look up the combo box node
     */
    @Test
    void doctorComboBoxEmptyOnNoServerTest(FxRobot robot) {
        // If the API call blew up, the combo box should have zero items — not null, just empty
        ComboBox<?> comboBox = robot.lookup("#doctorSelectComboBox").queryComboBox();
        assertTrue(comboBox.getItems().isEmpty());
    }
}
