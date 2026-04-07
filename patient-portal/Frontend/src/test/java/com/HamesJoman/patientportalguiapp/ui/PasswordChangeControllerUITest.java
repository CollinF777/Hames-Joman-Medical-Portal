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
 * Frontend tests for PasswordChangeController.
 * Tests UI behavior and input validation for the change-password view.
 *
 * This is JUST for ui testing, logic should be in the logic folder.
 * These are separated so that UI tests don't need to be run every time
 * only logic is being changed — keeps the feedback loop fast.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class PasswordChangeControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the Change Password view as the default screen.
     *
     * @param stage the main JavaFX stage provided by TestFX
     * @throws Exception if the FXML fails to load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/change-password.fxml")
        );
        Scene scene = new Scene(loader.load(), 300, 350);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that clicking Confirm with no fields filled in shows the correct error message.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void emptyFieldsShowsErrorTest(FxRobot robot) {
        robot.clickOn("Confirm");
        FxAssert.verifyThat("#passwordStatusLabel", LabeledMatchers.hasText("Please fill in all fields"));
    }

    /**
     * Verify that filling only the current password field and clicking Confirm
     * still shows the all-fields-required error, since new and confirm are still blank.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void onlyCurrentPasswordFilledShowsErrorTest(FxRobot robot) {
        robot.clickOn("#currentPasswordField").write("currentPass");
        robot.clickOn("Confirm");
        FxAssert.verifyThat("#passwordStatusLabel", LabeledMatchers.hasText("Please fill in all fields"));
    }

    /**
     * Verify that when the new password and confirm password fields don't match,
     * the mismatch error is displayed rather than attempting anything server-side.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void passwordsMismatchShowsErrorTest(FxRobot robot) {
        robot.clickOn("#currentPasswordField").write("currentPass");
        robot.clickOn("#newPasswordField").write("newPassword1");
        robot.clickOn("#confirmPasswordField").write("differentPassword");
        robot.clickOn("Confirm");
        FxAssert.verifyThat("#passwordStatusLabel", LabeledMatchers.hasText("New passwords do not match."));
    }

    /**
     * Verify that when the new password is identical to the current password,
     * the controller catches it and shows the appropriate error instead of accepting it.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void newPasswordSameAsCurrentShowsErrorTest(FxRobot robot) {
        // All three fields get the same value — the controller should reject this
        robot.clickOn("#currentPasswordField").write("samePassword");
        robot.clickOn("#newPasswordField").write("samePassword");
        robot.clickOn("#confirmPasswordField").write("samePassword");
        robot.clickOn("Confirm");
        FxAssert.verifyThat("#passwordStatusLabel", LabeledMatchers.hasText("New password must be different."));
    }

    /**
     * Verify that when all fields are valid but no backend is running, the controller
     * catches the connection error and displays the appropriate message.
     * No backend running, so a real attempt will throw a ConnectException and the
     * controller catches it and shows this message.
     *
     * @param robot TestFX robot that simulates user interaction with the UI
     */
    @Test
    void validInputWithNoServerShowsConnectionErrorTest(FxRobot robot) {
        robot.clickOn("#currentPasswordField").write("oldPassword");
        robot.clickOn("#newPasswordField").write("newPassword");
        robot.clickOn("#confirmPasswordField").write("newPassword");
        robot.clickOn("Confirm");
        FxAssert.verifyThat("#passwordStatusLabel", LabeledMatchers.hasText("Couldn't connect to server"));
    }
}
