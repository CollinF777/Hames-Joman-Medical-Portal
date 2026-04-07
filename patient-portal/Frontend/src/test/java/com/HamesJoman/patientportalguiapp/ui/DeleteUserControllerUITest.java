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
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Frontend tests for DeleteUserController.
 * Tests UI state on load and the behavior when no backend is reachable.
 *
 * The delete user popup is admin-only, so we care a lot about the guard rails here —
 * a delete button that fires without a selection would be a bad time for everyone.
 * These tests make sure the controller keeps things locked down until a user is actually
 * chosen from the combo box.
 *
 * No backend will be running during these tests, which is intentional — we want to
 * verify that the error handling path works correctly when the server is unreachable.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class DeleteUserControllerUITest {

    /**
     * Build the JavaFX stage for each test using the Delete User admin view.
     * If this throws, something is very wrong with the FXML path or loader setup.
     *
     * @param stage the primary JavaFX stage provided by the ApplicationExtension
     * @throws Exception if the FXML file fails to load
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/delete-user-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 420, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verify that the delete button is disabled when the view first loads.
     *
     * The controller calls setEditFieldsDisabled(true) inside initialize(), which
     * should immediately disable the delete button. No user has been selected yet
     * so there is nothing to delete — the button should absolutely not be clickable.
     *
     * @param robot TestFX robot (not used here, but required by the runner for @Test methods)
     */
    @Test
    void deleteButtonDisabledInitiallyTest(FxRobot robot) {
        // No interaction needed — this is purely a load-state check
        FxAssert.verifyThat("#deleteButton", NodeMatchers.isDisabled());
    }

    /**
     * Verify that the user combo box is empty when the server is unreachable.
     *
     * On initialize(), loadUsers() fires and attempts to call ApiClient.getAllUsers().
     * With no backend running that call will throw, the catch block runs, and the
     * combo box should be left with zero items. We look up the node directly from
     * the scene graph and check the item list to make sure nothing snuck in.
     *
     * @param robot TestFX robot used to look up the combo box node from the scene graph
     */
    @Test
    void userComboBoxEmptyOnNoServerTest(FxRobot robot) {
        // Grab the actual ComboBox node so we can inspect its items list
        ComboBox<?> comboBox = robot.lookup("#userSelectComboBox").queryComboBox();
        assertTrue(comboBox.getItems().isEmpty(),
                "Combo box should have no items when the server is unreachable");
    }

    /**
     * Verify that the action text label shows the correct error message when the
     * server connection fails during the initial loadUsers() call.
     *
     * The controller's catch block is supposed to set actionText to
     * "Couldn't connect to server" — this test makes sure that path is actually wired up
     * and visible to the user instead of silently swallowing the exception.
     *
     * @param robot TestFX robot (not used directly, required by the test runner)
     */
    @Test
    void serverConnectionFailsOnLoadTest(FxRobot robot) {
        // No backend is running so the catch block should have already fired by the time
        // the stage finished loading — nothing extra to do here
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
    }
}
