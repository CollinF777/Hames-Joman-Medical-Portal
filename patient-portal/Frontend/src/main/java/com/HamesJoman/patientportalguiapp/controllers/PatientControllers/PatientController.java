package com.HamesJoman.patientportalguiapp.controllers.PatientControllers;

import com.HamesJoman.patientportalguiapp.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PatientController {

    @FXML
    private GridPane dashboardPane;

    @FXML
    private GridPane appointmentsPane;

    @FXML
    private GridPane passwordPane;

    @FXML
    private Button logoutButton;

    @FXML
    private void showDashboard() {
        dashboardPane.setVisible(true);
        dashboardPane.setManaged(true);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    private void showAppointments() {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(true);
        appointmentsPane.setManaged(true);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    private void showPassword() {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(true);
        passwordPane.setManaged(true);
    }

    @FXML
    public void onLogoutButtonClick() {
        // Wipe session data
        SessionManager.getInstance().clear();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/login-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 700, 400);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Patient Portal");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}