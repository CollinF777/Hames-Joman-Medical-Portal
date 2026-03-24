package com.HamesJoman.patientportalguiapp.controllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpResponse;

public class LoginController {
    @FXML
    private Label actionText;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void onLoginButtonClick() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            actionText.setText("Please enter all fields");
            return;
        }
        try {
            HttpResponse<String> response = ApiClient.login(usernameField.getText(), passwordField.getText());

            if (response.statusCode() == 200) {
                // This is how we are going to parse jsons
                ObjectMapper mapper = new ObjectMapper();
                /**
                 * This turns raw JSON string into a tree struct
                 * From the root node (JsonNode) you can pull any field out of it by name
                 */
                JsonNode json = mapper.readTree(response.body());
                // So this pulls the role field from the JSON tree
                String role = json.get("role").asText();

                // Populate session
                SessionManager.getInstance().setUser(
                    json.get("id").asInt(),
                    json.get("firstName").asText(),
                    json.get("lastName").asText(),
                    json.get("username").asText(),
                    json.get("role").asText()
                );
                openDashboard(role);
            }
            else if (response.statusCode() == 401) {
                actionText.setText("Invalid Username or Password");
            }
            else {
                actionText.setText("Error: " + response.statusCode());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    private void openDashboard(String role) {
        try {
            String fxmlFile;
            String title;

            switch (role.toLowerCase()) {
                case "admin":
                    fxmlFile = "admin-view.fxml";
                    title = "Admin Dashboard";
                    break;
                case "doctor":
                    fxmlFile = "doctor-view.fxml";
                    title = "Doctor Dashboard";
                    break;
                case "patient":
                    fxmlFile = "patient-view.fxml";
                    title = "Patient Dashboard";
                    break;
                default:
                    actionText.setText("Invalid role: " + role);
                    return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/" + fxmlFile)
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.sizeToScene();     // 🔥 important
            stage.centerOnScreen();
        } catch (Exception e) {
            actionText.setText("Failed to load dashboard");
            e.printStackTrace();
        }
    }
}
