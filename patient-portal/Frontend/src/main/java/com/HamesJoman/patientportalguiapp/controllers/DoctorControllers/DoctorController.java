package com.HamesJoman.patientportalguiapp.controllers.DoctorControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class DoctorController {

    @FXML public DatePicker filterFromDate;
    @FXML public DatePicker filterToDate;
    @FXML public ComboBox filterPatientComboBox;
    @FXML private Label nextApptLabel;
    @FXML private Label totalApptsLabel;
    @FXML private ListView<String> appointmentListView;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<String> appointmentList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome back, Dr. " + SessionManager.getInstance().getFullName() + "!");
        updateSearchResults();
    }

    @FXML
    private void onChangePasswordWindowOpen() {
        try {
            // Adjust the path to where you saved your new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/HamesJoman/patientportalguiapp/change-password.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();

            stage.setTitle("Account Security");
            stage.initModality(Modality.APPLICATION_MODAL); // Locks the dashboard until closed
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    public void onFiltersChanged() {
        updateSearchResults();
    }

    @FXML
    public void onClearFilters() {
        filterFromDate.setValue(null);
        filterToDate.setValue(null);
        filterPatientComboBox.setValue(null);
        updateSearchResults();
    }

    public void updateSearchResults() {
        // Update appointment info
        appointmentList.clear();
        try {
            HttpResponse<String> response = ApiClient.getAppointmentsByDoctor(SessionManager.getInstance().getUserId());
            if (response.statusCode() == 200) {
                JsonNode appointments = mapper.readTree(response.body());
                if (appointments.isEmpty()) {
//                    outputBox.setText("This doctor has no appointments");
                    return;
                }

                for (JsonNode apt : appointments) {
                    LocalDate date = LocalDate.parse(apt.get("date").asText());
                    // Probably more elegant way to do this but it works for now
                    if ((filterToDate.getValue() == null || date.isBefore(filterToDate.getValue()) || date.isEqual(filterToDate.getValue())) && (filterFromDate.getValue() == null || date.isAfter(filterFromDate.getValue()) || date.isEqual(filterFromDate.getValue()))) {
                        String startTime = apt.get("startTime").asText();
                        String endTime = apt.get("endTime").asText();
                        String status = apt.get("status").asText();
                        int    aptId = apt.get("id").asInt();

                        JsonNode patientNode = apt.get("patient");
                        // Patient may be null if deleted
                        String patientInfo = (patientNode == null || patientNode.isNull())
                                ? "Deleted Patient" : patientNode.get("firstName").asText() + " " +
                                patientNode.get("lastName").asText() + " (ID: " + patientNode.get("id").asInt() + ")";
                        appointmentList.add(String.format("ID: %d | %s | %s–%s | Patient: %s | Status: %s%n",
                                aptId, date, startTime, endTime, patientInfo, status));
                    }
                }

                appointmentListView.setItems(appointmentList);
                totalApptsLabel.setText(appointmentList.size() + " Total Appointments");

                // Set next appointment
                int i = 0;
                while (i < appointments.size() && LocalDate.now().isBefore(
                        LocalDate.parse(appointments.get(i).get("date").asText())
                )) {
                    System.out.println(appointments.get(i));
                    i++;
                }
                i--;
                System.out.println(i);
                if (i < appointments.size()) {
                    JsonNode apt = appointments.get(i);
                    JsonNode patientNode = apt.get("patient");
                    String patientInfo = (patientNode == null || patientNode.isNull())
                            ? "Deleted Patient" : patientNode.get("firstName").asText() + " " +
                            patientNode.get("lastName").asText();
                    nextApptLabel.setText((patientInfo + " " + apt.get("startTime").toString() + " - " + apt.get("endTime")).replace("\"",""));
                } else {
                    nextApptLabel.setText("No Upcoming Appointments");
                }

            }
            else {
                appointmentList.add("Failed to fetch appointments: " + response.statusCode() + "\n" + response.body());
                appointmentListView.setItems(appointmentList);
            }
        } catch (Exception e) {
            appointmentList.add("Couldn't connect to server");
            appointmentListView.setItems(appointmentList);
            e.printStackTrace();
        }
    }
}
