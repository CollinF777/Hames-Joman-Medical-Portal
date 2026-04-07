package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;

/**
 * Controller for handling the create user view
 *
 * @author Collin Fair
 */
public class CreateUserController {
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
    private ComboBox<String> roleComboBox;

    @FXML
    private Button backButton;

    /**
     * Set values for the role combo box, default is Patient
     */
    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Patient", "Doctor", "Admin");
        roleComboBox.setValue("Patient");
    }

    /**
     * Creates user on button click if all fields are filled out properly
     */
    @FXML
    private void onCreateUserButtonClick() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()
                || roleComboBox.getValue() == null) {
            actionText.setText("Please enter all fields");
            return;
        }

        try {
            HttpResponse<String> response = ApiClient.createUser(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    roleComboBox.getValue()
            );

            if (response.statusCode() == 200) {
                actionText.setText(roleComboBox.getValue() + " created successfully");
                firstNameField.clear();
                lastNameField.clear();
                usernameField.clear();
                passwordField.clear();
                roleComboBox.setValue("Patient");
            }
            else {
                actionText.setText("Failed to create user: " + response.statusCode());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    /**
     * Goes back to default admin view on back button click
     */
    @FXML
    private void onBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}