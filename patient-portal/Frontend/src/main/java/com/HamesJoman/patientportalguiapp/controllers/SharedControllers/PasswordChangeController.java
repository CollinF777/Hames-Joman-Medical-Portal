package com.HamesJoman.patientportalguiapp.controllers.SharedControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import java.net.http.HttpResponse;

public class PasswordChangeController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordStatusLabel;

    @FXML
    private void onChangePasswordButtonClick() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isBlank() || newPass.isBlank() || confirm.isBlank()) {
            setStatus("Please fill in all fields", false);
            return;
        }

        if (!newPass.equals(confirm)) {
            setStatus("New passwords do not match.", false);
            return;
        }

        if (newPass.equals(current)) {
            setStatus("New password must be different.", false);
            return;
        }

        // Pulling the ID from the singleton SessionManager makes this universal
        int userId = SessionManager.getInstance().getUserId();

        try {
            HttpResponse<String> response = ApiClient.changePassword(userId, current, newPass);

            switch (response.statusCode()) {
                case 200 -> {
                    setStatus("Password successfully changed", true);
                    clearFields();
                }
                case 401 -> setStatus("Current password is incorrect.", false);
                case 404 -> setStatus("User not found. Try logging in again.", false);
                default -> setStatus("Server error (" + response.statusCode() + ")", false);
            }
        } catch (Exception e) {
            setStatus("Couldn't connect to server", false);
            e.printStackTrace();
        }
    }

    private void setStatus(String message, boolean success) {
        passwordStatusLabel.setText(message);
        passwordStatusLabel.setStyle(success ? "-fx-text-fill: #309423;" : "-fx-text-fill: #cc0000;");
    }

    private void clearFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}