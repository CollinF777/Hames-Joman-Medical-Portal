package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.http.HttpResponse;

public class DeleteUserController {
    @FXML
    private Label actionText;

    @FXML
    private ComboBox<String> userSelectComboBox;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private final ObjectMapper mapper = new ObjectMapper();

    // Start with no id selected
    private int selectedUserId = -1;

    @FXML
    public void initialize() {
        setEditFieldsDisabled(true);
        loadUsers();

        // Autofill fields with a users info when selected
        userSelectComboBox.setOnAction(e -> onUserSelected());
    }

    private void loadUsers() {
        try {
            HttpResponse<String> response = ApiClient.getAllUsers();

            if (response.statusCode() == 200) {
                JsonNode users = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for (JsonNode user: users) {
                    int id = user.get("id").asInt();
                    String name = user.get("firstName").asText() + " " + user.get("lastName").asText();
                    items.add(id + " - " + name);
                }

                userSelectComboBox.setItems(items);
            }
            else {
                actionText.setText("Failed to load users: " + response.statusCode());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    private void onUserSelected() {
        String selected = userSelectComboBox.getValue();
        if (selected == null) {
            return;
        }

        // Entry format is "id - name" So we want everything prior to " - "
        int id = Integer.parseInt(selected.split(" - ")[0].trim());
        selectedUserId = id;

        try {
            HttpResponse<String> response = ApiClient.getUserById(String.valueOf(id));

            if (response.statusCode() == 200) {
                JsonNode json = mapper.readTree(response.body());
                setEditFieldsDisabled(false);
                actionText.setText("Are you sure you want to DELETE user " + id + "?\nThis action cannot be undone.");
            }
            else {
               actionText.setText("Failed to load user: " + response.statusCode());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    private void setEditFieldsDisabled(boolean disabled) {
        deleteButton.setDisable(disabled);
    }

    @FXML
    private void onDeleteButtonClick() {
        if (selectedUserId == -1) {
            actionText.setText("Please select a user");
            return;
        }

        try {
            HttpResponse<String> response = ApiClient.deleteUser(
                    String.valueOf(selectedUserId)
            );

            if (response.statusCode() == 200) {
                actionText.setText("User successfully deleted");
                // Refresh drop down in case user has new name
                loadUsers();
            }
            else {
                actionText.setText("Deletion failed: " + response.statusCode());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }
    @FXML
    private void onBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
