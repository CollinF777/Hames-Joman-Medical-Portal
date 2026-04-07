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
 * Frontend tests for CreateUserController.
 * Tests UI behavior and input validation on the Create User popup.
 *
 * This is JUST for ui testing, logic should be in the logic folder.
 * Separated so that logic tests can be run independently of the UI tests,
 * which is both faster and more targeted when debugging one layer at a time.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class CreateUserControllerUITest {

    /**
     * Build the JavaFX stage for each test run with the Create User view as the screen.
     *
     * @param stage the main JavaFX stage
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/create-user-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that clicking "Create User" with absolutely nothing filled in shows the
     * correct "fill everything in" error message on the action label.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void emptyFieldsShowsErrorTest(FxRobot robot) {
        robot.clickOn("Create User");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Verify that filling only the first name field and then submitting still triggers
     * the validation error, since the remaining fields are all still empty.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void onlyFirstNameFilledShowsErrorTest(FxRobot robot) {
        robot.clickOn("#firstNameField").write("John");
        robot.clickOn("Create User");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Verify that filling only the last name field and then submitting still triggers
     * the validation error, since the remaining fields are all still empty.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void onlyLastNameFilledShowsErrorTest(FxRobot robot) {
        robot.clickOn("#lastNameField").write("Doe");
        robot.clickOn("Create User");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Verify that leaving the password blank while having first name, last name, and username
     * filled in still shows the missing-fields error. Every field must be present.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void missingPasswordShowsErrorTest(FxRobot robot) {
        robot.clickOn("#firstNameField").write("John");
        robot.clickOn("#lastNameField").write("Doe");
        robot.clickOn("#usernameField").write("jdoe1");
        // Intentionally leaving #passwordField empty to trigger the validation check
        robot.clickOn("Create User");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Verify that when all fields are properly filled in, the controller moves past
     * the validation step and attempts a real server call. Since no backend is running,
     * it should catch the connection exception and show the appropriate error message.
     *
     * The roleComboBox is intentionally left alone here since it already defaults to
     * "Patient" — so this test is valid without touching it.
     *
     * @param robot TestFX testing robot to simulate user interactions
     */
    @Test
    void allFieldsFilledShowsConnectionErrorTest(FxRobot robot) {
        // No backend running, so the controller will throw a ConnectException
        // and show this message once all validation passes
        robot.clickOn("#firstNameField").write("John");
        robot.clickOn("#lastNameField").write("Doe");
        robot.clickOn("#usernameField").write("jdoe1");
        robot.clickOn("#passwordField").write("password123");
        robot.clickOn("Create User");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
    }
}
