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
 * Frontend tests for SearchApptByPatientController.
 * Tests UI behavior on the Search Appointments by Patient view, specifically
 * verifying that the output box shows a connection error on load and that
 * the patient combo box is empty when no backend is reachable.
 *
 * This is JUST for ui testing, logic should be in the logic folder.
 * Separated so that logic tests can be run independently of the UI tests,
 * which is both faster and more targeted when debugging one layer at a time.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class SearchApptByPatientControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the Search by Patient view as the screen.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/search-appt-by-patient-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that when no backend is running, {@code loadPatients()} catches the
     * connection exception during {@code initialize()} and writes the appropriate
     * error message into {@code outputBox}. The user should know right away that
     * something went wrong rather than just staring at an empty screen.
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
     * Verify that the patient select combo box is completely empty when no backend is
     * available. Since {@code loadPatients()} failed to fetch any users, there should
     * be nothing to populate the list with — zero items, not null.
     *
     * @param robot TestFX testing robot used to look up the combo box node
     */
    @Test
    void patientComboBoxEmptyOnNoServerTest(FxRobot robot) {
        // If the API call failed, the combo box should have zero items — not null, just empty
        ComboBox<?> comboBox = robot.lookup("#patientSelectComboBox").queryComboBox();
        assertTrue(comboBox.getItems().isEmpty());
    }
}
