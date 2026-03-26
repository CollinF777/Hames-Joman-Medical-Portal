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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DoctorController {

    @FXML private Label nextApptLabel;
    @FXML private Label totalApptsLabel;
    @FXML private ListView appointmentListView;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<String> appointmentList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome back, Dr. " + SessionManager.getInstance().getFullName() + "!");

        // Update appointment info
        try {
            HttpResponse<String> response = ApiClient.getAppointmentsByDoctor(SessionManager.getInstance().getUserId());

            if (response.statusCode() == 200) {
                JsonNode appointments = mapper.readTree(response.body());

                if (appointments.isEmpty()) {
//                    outputBox.setText("This doctor has no appointments");
                    return;
                }

                for (JsonNode apt : appointments) {
                    String date = apt.get("date").asText();
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

                appointmentListView.setItems(appointmentList);
                totalApptsLabel.setText(appointmentList.size() + " Total Appointments");

                // Set next appointment
                LocalDateTime now = LocalDateTime.now();
                JsonNode nextAppointment = null;
                LocalDateTime nextAppointmentTime = null;

                for (JsonNode apt : appointments) {
                    String dateS = apt.get("date").asText();
                    String endS = apt.get("endTime").asText();

                    LocalDate date = LocalDate.parse(dateS);
                    LocalTime end = LocalTime.parse(endS);

                    LocalDateTime aptEnd = LocalDateTime.of(date, end);

                    // Make sure its not showing past appointments
                    if (aptEnd.isBefore(now)) {
                        continue;
                    }

                    // Use the start time to figure out next apt
                    String startS = apt.get("startTime").asText();
                    LocalTime start = LocalTime.parse(startS);
                    LocalDateTime aptStart = LocalDateTime.of(date, start);

                    // Find the earliest apt coming up
                    if (nextAppointment == null || aptStart.isBefore(nextAppointmentTime)) {
                        nextAppointment = apt;
                        nextAppointmentTime = aptStart;
                    }
                }
                if (nextAppointment != null) {
                    JsonNode patientNode = nextAppointment.get("patient");

                    String patientInfo = (patientNode == null || patientNode.isNull()) ? "Deleted Patient" :
                            patientNode.get("firstName").asText() + " " + patientNode.get("lastName").asText();

                    nextApptLabel.setText(patientInfo + " " + nextAppointment.get("startTime").asText() + " - " +
                                    nextAppointment.get("endTime").asText() + " on " + nextAppointment.get("date").asText());
                }
                else {
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
}