package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {
    @FXML
    private Label actionText;

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

    @FXML
    public void onExitButtonClick() {
        System.exit(0);
    }

}