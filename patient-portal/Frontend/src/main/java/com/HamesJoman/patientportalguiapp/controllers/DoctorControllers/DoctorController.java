package com.HamesJoman.patientportalguiapp.controllers.DoctorControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;

public class DoctorController {

    @FXML
    private StackPane contentStack;

    @FXML
    private GridPane dashboardPane;

    @FXML
    private GridPane appointmentsPane;

    @FXML
    private GridPane passwordPane;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private  PasswordField confirmPasswordField;

    @FXML
    private Label passwordStatusLabel;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        // Initialization logic if required
    }

    @FXML
    public void showDashboard(ActionEvent event) {
        dashboardPane.setVisible(true);
        dashboardPane.setManaged(true);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    public void showAppointments(ActionEvent event) {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(true);
        appointmentsPane.setManaged(true);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    public void showPassword(ActionEvent event) {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(true);
        passwordPane.setManaged(true);
    }

    /**
     * Handles event on Change Password button click
     * Validates field then sends to the backend
     */
    @FXML
    private void onChangePasswordButtonClick() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isBlank() || newPass.isBlank() || confirm.isBlank()) {
            setPasswordStatus("Please fill in all fields", false);
            return;
        }

        if (!newPass.equals(confirm)) {
            setPasswordStatus("New passwords do not match.", false);
            return;
        }

        if (newPass.equals(current)) {
            setPasswordStatus("New password must not match old password", false);
            return;
        }

        int userId = SessionManager.getInstance().getUserId();

        try {
            HttpResponse<String> response = ApiClient.changePassword(userId, current, newPass);

            switch (response.statusCode()) {
                case 200:
                    setPasswordStatus("Password successfully changed", true);
                    currentPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    break;
                case 401:
                    setPasswordStatus("Current password is incorrect.", false);
                    break;
                case 404:
                    setPasswordStatus("User not found. Please try loggin in again.", false);
                    break;
                default:
                    setPasswordStatus("Server error (" + response.statusCode() + ")", false);
            }
        } catch (Exception e) {
            setPasswordStatus("Couldnt connect to server", false);
            e.printStackTrace();
        }
    }

    private void setPasswordStatus(String message, boolean success) {
        passwordStatusLabel.setText(message);
        passwordStatusLabel.setStyle(success ? "-fx-text-fill: #309423;" : "-fx-text-fill: #cc0000;");
    }

    @FXML
    public void onLogoutButtonClick() {
        // Wipe session
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
