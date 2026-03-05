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
import java.util.ArrayList;

public class SearchApptByDoctorController {
    @FXML
    private Button backButton;

    @FXML
    private TextArea outputBox;

    @FXML
    private ComboBox<String> doctorSelectComboBox;

    // This is how we parse our JSON strings into something usable
    private final ObjectMapper mapper = new ObjectMapper();

    private int selectedDoctorId = -1;

    @FXML
    public void initialize() {
        loadDoctors();
        doctorSelectComboBox.setOnAction(e -> onDoctorSelected());
    }

    public void loadDoctors() {
        try {
            HttpResponse<String> response = ApiClient.getAllUsers();

            if (response.statusCode() == 200) {
                /**
                 * We are taking the JSON string and putting it into a tree where we can pull the data we need
                 * The tree would look like
                 * THIS IS FOR USERS, APPOINTMENT TREES ARE A LOT BIGGER AND I DONT WANNA DRAW THAT
                 * Appointment trees follow the same logic but the root would be an ObjectNode with some of its
                 * children being values like its id, date, time, etc and others being ObjectNodes like patient
                 *              ROOT (ArrayNode)
                 *              /               \
                 *             [0]              [1] (ObjectNode)
                 *             /|\              /|\
                 *    "id" "firstName" "role"   Same as left
                 *      |       |         |
                 *      2    "Collin"  "Admin"
                 *
                 * MB Thought I explained this in the other controller but I was wrong
                 */
                JsonNode users = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for (JsonNode user : users) {
                    if ("Doctor".equals(user.get("role").asText())) {
                        int id = user.get("id").asInt();
                        String name = user.get("firstName").asText() + " " + user.get("lastName").asText();
                        items.add(id + " - " + name);
                    }
                }

                doctorSelectComboBox.setItems(items);
            }
            else {
                outputBox.setText("Couldn't load doctors: " + response.statusCode());
            }
        } catch (Exception e) {
            outputBox.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }
    private void onDoctorSelected() {
        String selected = doctorSelectComboBox.getValue();
        if (selected == null) {
            return;
        }
        selectedDoctorId = Integer.parseInt(selected.split(" - ")[0].trim());
        onSearchButtonClick();
    }
    @FXML
    private void onBackButtonClick() {
        // Close the current window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSearchButtonClick() {
        if (selectedDoctorId == -1) {
            outputBox.setText("Please select a doctor");
            return;
        }

        try {
            HttpResponse<String> response = ApiClient.getAppointmentsByDoctor(selectedDoctorId);

            if (response.statusCode() == 200) {
                JsonNode appointments = mapper.readTree(response.body());

                if (appointments.isEmpty()) {
                    outputBox.setText("This doctor has no appointments");
                    return;
                }

                StringBuilder s = new StringBuilder();
                s.append("Appointments for: ").append(doctorSelectComboBox.getValue()).append("\n");
                s.append("-".repeat(50)).append("\n");

                for (JsonNode apt : appointments) {
                    String date = apt.get("date").asText();
                    String startTime = apt.get("startTime").asText();
                    String endTime = apt.get("endTime").asText();
                    String status = apt.get("status").asText();
                    int    aptId = apt.get("id").asInt();

                    JsonNode patient = apt.get("patient");
                    String patientName = patient.get("firstName").asText() + " " + patient.get("lastName").asText();
                    int patientId = patient.get("id").asInt();

                    s.append(String.format("ID: %d | %s | %s–%s | Patient: %s (ID: %d) | Status: %s%n",
                            aptId, date, startTime, endTime, patientName, patientId, status));
                }

                outputBox.setText(s.toString());
            }
            else {
                outputBox.setText("Failed to fetch appointments: " + response.statusCode() + "\n" + response.body());
            }
        } catch (Exception e) {
            outputBox.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }
}
