package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.List;

public class CreateAppointmentController {

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private ComboBox<String> patientComboBox;

    @FXML
    private ComboBox<String> doctorComboBox;

    @FXML
    private Label actionText;

    @FXML
    private Spinner<Integer> startHourSpinner;

    @FXML
    private Spinner<Integer> startMinuteSpinner;

    @FXML
    private Spinner<Integer> endHourSpinner;

    @FXML
    private Spinner<Integer> endMinuteSpinner;

    @FXML
    private Button backButton;

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        // Hour spinners
        startHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));

        endHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));

        // 15-minute minute spinner
        List<Integer> minutes = List.of(0, 15, 30, 45);

        startMinuteSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(minutes)));

        endMinuteSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(minutes)));

        // Auto set current time rounded to next 15 min
        setInitialTime();
        loadUsers();
    }

    private void setInitialTime() {
        LocalTime now = LocalTime.now();

        int minute = now.getMinute();
        int roundedMinute = ((minute + 14) / 15) * 15;

        int hour = now.getHour();

        // Handle case when rounding hits 60
        if (roundedMinute == 60) {
            roundedMinute = 0;
            hour++;
        }

        // Handle midnight overflow
        if (hour == 24) {
            hour = 0;
        }

        // Set start time
        startHourSpinner.getValueFactory().setValue(hour);
        startMinuteSpinner.getValueFactory().setValue(roundedMinute);

        // Set end time (start + 15 minutes)
        LocalTime endTime = LocalTime.of(hour, roundedMinute).plusMinutes(15);

        endHourSpinner.getValueFactory().setValue(endTime.getHour());
        endMinuteSpinner.getValueFactory().setValue(endTime.getMinute());
    }

    private void loadUsers() {
        try {
            HttpResponse<String> response = ApiClient.getAllUsers();

            if (response.statusCode() == 200) {
                JsonNode users = mapper.readTree(response.body());
                ObservableList<String> patients = FXCollections.observableArrayList();
                ObservableList<String> doctors  = FXCollections.observableArrayList();

                for (JsonNode user : users) {
                    int id = user.get("id").asInt();
                    String name = user.get("firstName").asText() + " " + user.get("lastName").asText();
                    String role = user.get("role").asText();

                    if ("Patient".equals(role)) {
                        patients.add(id + " - " + name);
                    } else if ("Doctor".equals(role)) {
                        doctors.add(id + " - " + name);
                    }
                }

                patientComboBox.setItems(patients);
                doctorComboBox.setItems(doctors);
            }
            else {
                actionText.setText("Failed to load users: " + response.statusCode());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreateAppointmentButtonClick() {
        // Validate fields
        if (appointmentDatePicker.getValue() == null ||
                patientComboBox.getValue() == null ||
                doctorComboBox.getValue() == null) {

            actionText.setText("Please fill in all fields.");
            return;
        }

        // Get patient/doctor IDs
        int patientId = Integer.parseInt(patientComboBox.getValue().split(" - ")[0].trim());
        int doctorId = Integer.parseInt(doctorComboBox.getValue().split(" - ")[0].trim());

        String date = appointmentDatePicker.getValue().toString();
        // Get times from spinners
        LocalTime startTime = LocalTime.of(
                startHourSpinner.getValue(),
                startMinuteSpinner.getValue()
        );

        LocalTime endTime = LocalTime.of(
                endHourSpinner.getValue(),
                endMinuteSpinner.getValue()
        );

        // Validate time logic
        if (!endTime.isAfter(startTime)) {
            actionText.setText("End time must be after start time.");
            return;
        }

        // This formats the time into a string
        // Ima keep it a buck I did not know how to format this, the formatting part is done by an ai
        String startStr = String.format("%02d:%02d", startTime.getHour(), startTime.getMinute());
        String endStr   = String.format("%02d:%02d", endTime.getHour(),   endTime.getMinute());

        try {
            HttpResponse<String> response = ApiClient.createAppointment(
                 date, startStr, endStr, patientId, doctorId
            );

            switch(response.statusCode()) {
                case 201:
                    actionText.setText("Appointment created successfully!");
                    patientComboBox.setValue(null);
                    doctorComboBox.setValue(null);
                    appointmentDatePicker.setValue(null);
                    setInitialTime();
                    break;
                case 400:
                    actionText.setText("Invalid input: " + response.body());
                    break;
                case 404:
                    actionText.setText("Patient or doctor not found: " + response.body());
                    break;
                case 409:
                    actionText.setText("Time conflict: " + response.body());
                    break;
                default:
                    actionText.setText("Server error (" + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }


    @FXML
    private void onBackButtonClick() {
        // Close the current window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
