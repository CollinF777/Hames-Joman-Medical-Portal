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

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Frontend tests for ConfirmController
 *
 * @author Collin Fair
 */
@ExtendWith(ApplicationExtension.class)
public class ConfirmControllerUITest {
    private Stage primaryStage;

    /**
     * Build the stage with the confirm view as the scene
     *
     * @param stage the main JavaFX stage
     * @throws Exception if it doesnt load
     */
    @Start
    void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/HamesJoman/patientportalguiapp/confirmation-view.fxml"
        ));
        Scene scene = new Scene(loader.load(), 200, 100);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that both yes and no buttons are shown when the view is loaded
     *
     * @param robot The robot that will click on the button
     */
    @Test
    void bothButtonsVisibleTest(FxRobot robot) {
        robot.lookup("Yes").queryButton();
        robot.lookup("No").queryButton();
    }

    /**
     * When the view is loaded there should be text asking that the user is sure
     */
    @Test
    void headerShownOnLoadTest() {
        FxAssert.verifyThat("Are you sure?", LabeledMatchers.hasText("Are you sure?"));
    }

    /**
     * When a user clicks yes the view should close
     *
     * @param robot The robot that will click on the button
     */
    @Test
    void yesButtonClosesViewTest(FxRobot robot) {
        robot.clickOn("Yes");
        // Clicking yes should close out the view
        assertFalse(primaryStage.isShowing());
    }

    /**
     * When a user clicks no the view should close
     *
     * @param robot The robot that will click on the button
     */
    @Test
    void noButtonClosesViewTest(FxRobot robot) {
        robot.clickOn("No");
        // Clicking no should close out the view
        assertFalse(primaryStage.isShowing());
    }
}
