package com.HamesJoman.patientportalguiapp.ui;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.control.Label;
import org.testfx.util.WaitForAsyncUtils;
import java.time.Duration;

/**
 * Frontend tests for AdminController.
 * Tests UI behavior for the admin dashboard navigation buttons, specifically
 * verifying that clicking each nav button correctly updates the actionText
 * label in the main admin view.
 *
 * This is JUST for ui testing
 *
 * @author Nathan Amidon
 */
@ExtendWith(ApplicationExtension.class)
class AdminControllerUITest {

    /** The primary stage so we can scope lookups and skip it during cleanup. */
    private Stage primaryStage;

    /**
     * Build the JavaFX stage for each test run with the admin dashboard view as the default screen.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load (if this happens WE ARE COOKED)
     */
    @Start
    void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/com/HamesJoman/patientportalguiapp/admin-view.fxml"
            )
        );
        Scene scene = new Scene(loader.load(), 320, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * After each test, close any popup windows that a button may have opened so that
     * leftover stages don't bleed into the next test. We skip the primary stage since
     * that one sticks around for the full test class lifecycle.
     *
     * @throws Exception if the async JavaFX task is interrupted or throws
     */
    @AfterEach
    void closePopups() throws Exception {
        WaitForAsyncUtils.asyncFx(
            (Callable<Void>) () -> {
                // Snapshot the list first to avoid ConcurrentModificationException
                new ArrayList<>(Window.getWindows()).forEach(window -> {
                    if (
                        window instanceof Stage popupStage &&
                        popupStage != primaryStage
                    ) {
                        popupStage.close();
                    }
                });
                return null;
            }
        ).get();
    }

    /**
     * Helper that verifies the #actionText label in the admin view carries
     * the expected text.
     *
     * @param expected the exact string we expect the label to display
     */
    private void verifyAdminActionText(String expected) {
        // Flush any pending FX events so the setText() call from the button handler
        // is guaranteed to have run before we assert
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#actionText", LabeledMatchers.hasText(expected));
    }

    /**
     * Clicking "Search Appointments by Patient" should update the main view's
     * actionText label to reflect that a patient appointment search is in progress.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void searchPatientAppointmentsButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("Search Appointments by Patient");
        verifyAdminActionText("Searching Patient appointments");
    }

    /**
     * Clicking "Search Appointments by Doctor" should update the main view's
     * actionText label to reflect that a doctor appointment search is in progress.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void searchDoctorAppointmentsButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("Search Appointments by Doctor");
        verifyAdminActionText("Searching Doctor Appointments.");
    }

    /**
     * Clicking "New Appointment" should update the main view's actionText
     * label to indicate that the appointment creation flow has been triggered.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void newAppointmentButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("New Appointment");
        verifyAdminActionText("Creating a new Appointment");
    }

    /**
     * Clicking "Update Appointment" should update the main view's actionText
     * label to indicate that the appointment update flow has been triggered.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void updateAppointmentButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("Update Appointment");
        verifyAdminActionText("Updating an Appointment");
    }

    /**
     * Clicking "New User" should update the main view's actionText label to
     * indicate that the user creation flow has been triggered.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void newUserButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("New User");
        verifyAdminActionText("Creating a new User");
    }

    /**
     * Clicking "Delete User" should update the main view's actionText label to
     * indicate that the user deletion flow has been triggered.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void deleteUserButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("Delete User");
        verifyAdminActionText("Deleting a User");
    }

    /**
     * Clicking "Update User" should update the main view's actionText label to
     * indicate that the user update flow has been triggered.
     *
     * @param robot TestFX testing robot to click buttons
     */
    @Test
    void updateUserButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("Update User");
        verifyAdminActionText("Updating a User");
    }
}
