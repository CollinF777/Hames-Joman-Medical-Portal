package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for default admin view, mostly other buttons that lead to other views
 *
 * @author Collin Fair, Corey Suhr, and Liam Callahan
 */
public class AdminController {
    @FXML
    private Label actionText;

    /**
     * Open up the view for searching appointments by doctor on button click
     */
    @FXML
    protected void onDoctorSearchAptButtonClick() {
        actionText.setText("Searching Doctor Appointments.");

        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/search-appt-by-doctor-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 400, 400);

            Stage popup = new Stage();
            popup.setTitle("Search Doctor Appointments");
            popup.setScene(scene);
            popup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open up the view for searching appointments by patient on button click
     */
    @FXML
    protected void onPatientSearchAptButtonClick() {
        actionText.setText("Searching Patient appointments");

        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/search-appt-by-patient-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 400, 400);

            Stage popup = new Stage();
            popup.setTitle("Search Appointment by Patient");
            popup.setScene(scene);
            popup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open up the view for creating new appointments on button click
     */
    @FXML
    protected void onNewAptButtonClick() {
        actionText.setText("Creating a new Appointment");

        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/create-appt-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 400, 400);

            Stage popup = new Stage();
            popup.setTitle("Create Appointment");
            popup.setScene(scene);
            popup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open up the view for updating appointments on button click
     */
    @FXML
    public void onUpdateAptButtonClick() {
        actionText.setText("Updating an Appointment");

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/update-appt-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 400, 450);

            Stage popup = new Stage();
            popup.setTitle("Update Appointment");
            popup.setScene(scene);
            popup.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Open up the view for creating new users on button click
     */
    @FXML
    public void onCancelAptButtonClick() {
        actionText.setText("Cancelling an Appointment");

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/cancel-appt-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 400, 450);

            Stage popup = new Stage();
            popup.setTitle("Cancel Appointment");
            popup.setScene(scene);
            popup.show();
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void onNewUserButtonClick() {
        actionText.setText("Creating a new User");

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/create-user-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage popup = new Stage();
            popup.setTitle("Create User");
            popup.setScene(scene);
            popup.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open up the view for deleting users on button click
     */
    @FXML
    protected void onDeleteUserButtonClick() {
        actionText.setText("Deleting a User");

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/delete-user-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 400, 400);
            Stage popup = new Stage();
            popup.setTitle("Delete User");
            popup.setScene(scene);
            popup.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open up the view for updating users on button click
     */
    @FXML
    protected void onUpdateUserButtonClick() {
        actionText.setText("Updating a User");

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/update-user-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 400, 450);
            Stage popup = new Stage();
            popup.setTitle("Update User");
            popup.setScene(scene);
            popup.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes out the application on exit button click
     */
    @FXML
    public void onExitButtonClick() {
        System.exit(0);
    }

    /**
     * Clears the session data and takes user back to login view on button click
     */
    public void onLogoutButtonClick() {
        // Wipe session data
        SessionManager.getInstance().clear();

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/login-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 700, 400);
            Stage stage = (Stage) actionText.getScene().getWindow();
            stage.setTitle("Patient Portal");
            stage.setScene(scene);
            stage.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}