package edu.secourse.patientportalguiapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.util.List;

public class CreateAppointmentController {

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private TextField patientIdField;

    @FXML
    private TextField doctorIdField;

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


    @FXML
    private void onCreateAppointmentButtonClick() {

        // Validate fields
        if (appointmentDatePicker.getValue() == null ||
                patientIdField.getText().isEmpty() ||
                doctorIdField.getText().isEmpty()) {

            actionText.setText("Please fill in all fields.");
            return;
        }

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

        // If everything is valid
        actionText.setText("Appointment created successfully!");

        // Clear fields
        patientIdField.clear();
        doctorIdField.clear();
    }


    @FXML
    private void onBackButtonClick() {
        // Close the current window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
