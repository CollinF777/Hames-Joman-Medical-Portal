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

public class SearchApptByPatientController {

    @FXML
    private Button backButton;

    @FXML
    private TextArea outputBox;

    @FXML
    private ComboBox<String> patientSelectComboBox;

    private final ObjectMapper mapper = new ObjectMapper();

    private int selectedPatientId = -1;

    @FXML
    public void initialize() {
        loadPatients();
        patientSelectComboBox.setOnAction(e -> onPatientSelected());
    }

    private void loadPatients() {
        try {
            HttpResponse<String> response = ApiClient.getAllUsers();

            if (response.statusCode() == 200) {
                // Refer to SearchByDoctor for explanation
                JsonNode users = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for (JsonNode user : users) {
                    if ("Patient".equals(user.get("role").asText())) {
                        int id = user.get("id").asInt();
                        String name = user.get("firstName").asText() + " " + user.get("lastName").asText();
                        items.add(id + " - " + name);
                    }
                }
                patientSelectComboBox.setItems(items);
            }
            else {
                outputBox.setText("Failed to load patients: " + response.statusCode());
            }
        } catch (Exception e) {
            outputBox.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    private void onPatientSelected() {
        String selected = patientSelectComboBox.getValue();
        if (selected == null) {
            return;
        }
        selectedPatientId = Integer.parseInt(selected.split(" - ")[0].trim());
        onSearchButtonClick();
    }

    @FXML
    private void onSearchButtonClick() {
        if (selectedPatientId == -1) {
            outputBox.setText("Please select a patient");
            return;
        }

        try {
            HttpResponse<String> response = ApiClient.getAppointmentsByPatient(selectedPatientId);

            if (response.statusCode() == 200) {
                JsonNode appointments = mapper.readTree(response.body());

                if (appointments.isEmpty()) {
                    outputBox.setText("This patient has no appointments");
                    return;
                }

                StringBuilder s = new StringBuilder();
                s.append("Appointments for: ").append(patientSelectComboBox.getValue()).append("\n");
                s.append("─".repeat(50)).append("\n");

                for (JsonNode apt : appointments) {
                    String date = apt.get("date").asText();
                    String startTime = apt.get("startTime").asText();
                    String endTime = apt.get("endTime").asText();
                    String status = apt.get("status").asText();
                    int aptId = apt.get("id").asInt();

                    JsonNode doctorNode = apt.get("doctor");
                    // Doctor may be null if deleted
                    String doctorInfo = (doctorNode == null || doctorNode.isNull())
                            ? "Deleted Doctor" : doctorNode.get("firstName").asText() + " " +
                            doctorNode.get("lastName").asText() + " (ID: " + doctorNode.get("id").asInt() + ")";

                    s.append(String.format("ID: %d | %s | %s–%s | Doctor: %s | Status: %s%n",
                            aptId, date, startTime, endTime, doctorInfo, status));
                }
                outputBox.setText(s.toString());
            }
            else {
                outputBox.setText("Failed to fetch appointments: " + response.statusCode()
                        + "\n" + response.body());
            }
        } catch (Exception e) {
            outputBox.setText("Couldn't connect to server.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}