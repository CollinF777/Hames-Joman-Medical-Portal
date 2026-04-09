package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the admin view all users screen with role filtering
 *
 * @author Mohamed Musa and Collin Fair
 */
public class ViewUsersController {

    @FXML private ComboBox<String> roleFilterComboBox;
    @FXML private ListView<String> usersListView;
    @FXML private Label totalUsersLabel;
    @FXML
    private Button backButton;

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<String> userList = FXCollections.observableArrayList();
    private JsonNode allUsers;

    /**
     * Initialize filter with options sort by role and load all users
     */
    @FXML
    public void initialize() {
        roleFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "Admin", "Doctor", "Patient"
        ));
        roleFilterComboBox.setValue("All");
        roleFilterComboBox.setOnAction(e -> applyFilter());

        try {
            HttpResponse<String> response = ApiClient.getAllUsers();
            if (response.statusCode() == 200) {
                allUsers = mapper.readTree(response.body());
                applyFilter();
            } else {
                userList.add("Failed to fetch users: " + response.statusCode());
                usersListView.setItems(userList);
            }
        } catch (Exception e) {
            userList.add("Couldn't connect to server");
            usersListView.setItems(userList);
            e.printStackTrace();
        }
    }

    /**
     * Apply the filter so users only see what they want
     */
    private void applyFilter() {
        if (allUsers == null) return;
        userList.clear();

        String filter = roleFilterComboBox.getValue();
        List<JsonNode> filtered = new ArrayList<>();

        for (JsonNode user : allUsers) {
            String role = user.get("role").asText();
            boolean include = filter.equals("All") || role.equalsIgnoreCase(filter);
            if (include) filtered.add(user);
        }

        for (JsonNode user : filtered) {
            int id = user.get("id").asInt();
            String firstName = user.get("firstName").asText();
            String lastName = user.get("lastName").asText();
            String username = user.get("username").asText();
            String role = user.get("role").asText();
            userList.add(String.format("ID: %d | %s %s | Username: %s | Role: %s",
                    id, firstName, lastName, username, role));
        }

        usersListView.setItems(userList);
        totalUsersLabel.setText(filtered.size() + " Users");
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