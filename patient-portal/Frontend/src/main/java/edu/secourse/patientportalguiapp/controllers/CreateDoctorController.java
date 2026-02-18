package edu.secourse.patientportalguiapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateDoctorController {
    @FXML
    private Label actionText;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button backButton;

    @FXML
    private void onCreateDoctorButtonClick() {
        // Check if any field is empty
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            actionText.setText("Please enter all fields");
        } else {
            // All fields are filled then clear them
            firstNameField.clear();
            lastNameField.clear();
            usernameField.clear();
            passwordField.clear();

            actionText.setText("Doctor created successfully!");
        }
    }

    @FXML
    private void onBackButtonClick() {
        // Close the current window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
