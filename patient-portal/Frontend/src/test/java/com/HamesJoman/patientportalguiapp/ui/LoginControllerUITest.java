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
 * Frontend tests for LoginController.
 * Tests UI behavior and input validation.
 *
 * This is JUST for ui testing, logic should be the logic folder
 * I separated these as prior I was only getting logic errors but no ui errors
 * So I wanted to not have to run the ui tests every single time when it's not needed
 * Overall this is likely just a better way to do it anyways
 *
 * @author Collin Fair
 */
@ExtendWith(ApplicationExtension.class)
class LoginControllerUITest {
    /**
     * Build the JavaFX stage for each test run with the Login view as the default screen
     *
     * @param stage the main JavaFX stage
     * @throws Exception If the FXML doesn't load (if this happens WE ARE COOKED)
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
     * Verify that if no fields are filled in then the correct error message pops up
     *
     * @param robot Robot to allow us to click on buttons for testing, imagine the robot
     *              as if a user was actually just clicking all these things
     */
    @Test
    void emptyFieldsShowsErrorTest(FxRobot robot) {
        // I dont want to make comments to obvious but for the sake of teaching, this is how you test click buttons
        robot.clickOn("#loginButton");
        // Similar to JUnit assertions, you are verifying that some fxml node matches some kind of text
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * If just a username is entered then the proper error should show up
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void onlyUsernameFilledShowsErrorTest(FxRobot robot) {
        // Again, probably self-explanatory but .write() is how you get the robot to enter text
        robot.clickOn("#usernameField").write("dbjuiwkhbfiwfjc");
        robot.clickOn("#loginButton");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * If just a password is entered then the proper error should show up
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void onlyPasswordFilledShowsErrorTest(FxRobot robot) {
        robot.clickOn("#passwordField").write("fakePassword");
        robot.clickOn("#loginButton");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Please enter all fields"));
    }

    /**
     * Verify that if the server was down and the user tried connecting, the correct
     * error would be displayed
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void connectionFailureShowsErrorTest(FxRobot robot) {
        // No backend running, so a real attempt will throw ConnectException
        // and the controller catches it and shows this message
        robot.clickOn("#usernameField").write("fjnvcefnknf");
        robot.clickOn("#passwordField").write("fnikeaninf");
        robot.clickOn("#loginButton");
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
    }
}