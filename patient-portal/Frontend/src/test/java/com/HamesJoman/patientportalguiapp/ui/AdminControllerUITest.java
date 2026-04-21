package com.HamesJoman.patientportalguiapp.ui;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Frontend tests for AdminController.
 *
 * @author Nathan Amidon and Collin Fair
 */
@ExtendWith(ApplicationExtension.class)
class AdminControllerUITest {

    private Stage primaryStage;

    /**
     * Build the JavaFX stage for each test run with the admin dashboard view as the default screen.
     *
     * @param stage the main JavaFX stage provided by the TestFX extension
     * @throws Exception if the FXML doesn't load
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
                    new ArrayList<>(Window.getWindows()).forEach(window -> {
                        if (window instanceof Stage popupStage &&
                                popupStage != primaryStage) {
                            popupStage.close();
                        }
                    });
                    return null;
                }
        ).get();
    }

    /**
     * Reads #actionText directly from the primary stage's scene root.
     * This avoids the global TestFX lookup accidentally finding an
     * #actionText node inside a popup window that opened during the test.
     */
    private String getAdminActionText() {
        WaitForAsyncUtils.waitForFxEvents();
        Label label = (Label) primaryStage.getScene()
                .lookup("#actionText");
        return label != null ? label.getText() : "";
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
        assertEquals("Searching Patient appointments", getAdminActionText());
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
        assertEquals("Searching Doctor Appointments.", getAdminActionText());
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
        assertEquals("Creating a new Appointment", getAdminActionText());
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
        assertEquals("Updating an Appointment", getAdminActionText());
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
        assertEquals("Creating a new User", getAdminActionText());
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
        assertEquals("Updating a User", getAdminActionText());
    }

    /**
     * Clicking "View All Users" should update actionText to confirm the view was opened.
     *
     * @param robot TestFX robot
     */
    @Test
    void viewAllUsersButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("View All Users");
        assertEquals("Viewing all users.", getAdminActionText());
    }

    /**
     * Clicking "View All Appointments" should update actionText to confirm the view was opened.
     *
     * @param robot TestFX robot
     */
    @Test
    void viewAllAppointmentsButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("View All Appointments");
        assertEquals("Viewing all appointments.", getAdminActionText());
    }

    /**
     * Clicking "Cancel Appointment" should update actionText to indicate the cancel
     * flow has been triggered.
     *
     * @param robot TestFX robot
     */
    @Test
    void cancelAppointmentButtonUpdatesActionTextTest(FxRobot robot) {
        robot.clickOn("Cancel Appointment");
        assertEquals("Cancelling an Appointment", getAdminActionText());
    }
}