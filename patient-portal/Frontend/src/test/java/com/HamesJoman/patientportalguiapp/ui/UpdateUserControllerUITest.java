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
 * Frontend tests for UpdateUserController.
 * Tests UI state on initialization — disabled fields, disabled button, and the
 * server error message that fires when no backend is running.
 *
 * The controller locks every edit field down on startup until the admin actually
 * selects a user from the combo box, so a big chunk of what we're verifying here
 * is just that the "everything is off by default" behavior holds.
 *
 * Same deal as the other UI tests: no backend means the loadUsers() call is
 * going to blow up with a ConnectException, which the controller catches and
 * surfaces as the "Couldn't connect to server" message — handy for us.
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class UpdateUserControllerUITest {

    /**
     * Spin up the Update User view before each test.
     * 420x480 matches the size declared in the FXML so nothing gets clipped.
     *
     * @param stage the primary JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML fails to load (if that happens something has
     *                   gone very wrong and we have bigger problems)
     */
    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/update-user-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 420, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The Update button should be disabled right out of the gate.
     * The controller calls setEditFieldsDisabled(true) inside initialize(), which
     * sets updateButton.setDisable(true) — nothing should be clickable until a
     * user is chosen from the dropdown.
     *
     * @param robot TestFX robot (not used here, but required by the extension when
     *              the test method signature includes it — keeping it for consistency)
     */
    @Test
    void updateButtonDisabledInitiallyTest(FxRobot robot) {
        FxAssert.verifyThat("#updateButton", NodeMatchers.isDisabled());
    }

    /**
     * Every editable text field should be disabled on load.
     * There's no point letting an admin type into them before a user is even
     * selected, so the controller blanket-disables all four fields in initialize().
     * This test just makes sure that contract isn't accidentally broken.
     *
     * @param robot TestFX robot used to interact with the scene if needed
     */
    @Test
    void editFieldsDisabledInitiallyTest(FxRobot robot) {
        // All four input fields should be locked until a user is picked from the combo box
        FxAssert.verifyThat("#firstNameField", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#lastNameField", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#usernameField", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#passwordField", NodeMatchers.isDisabled());
    }

    /**
     * When the backend isn't running, loadUsers() will throw a ConnectException.
     * The controller catches it and updates actionText accordingly.
     * Since our test environment has no backend, this should trigger every time
     * and we can use it to confirm the error-handling path actually works.
     *
     * @param robot TestFX robot (not used here — the error appears on initialize,
     *              no interaction needed)
     */
    @Test
    void serverConnectionFailsOnLoadTest(FxRobot robot) {
        // No backend = ConnectException = controller sets this message, simple as that
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText("Couldn't connect to server"));
    }
}
