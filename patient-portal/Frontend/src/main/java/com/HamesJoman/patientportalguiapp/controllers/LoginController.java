package com.HamesJoman.patientportalguiapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Label actionText;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML private void onLoginButtonClick() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            actionText.setText("Please enter all fields");
        }
        else {
            usernameField.clear();
            passwordField.clear();
        }
    }
}
