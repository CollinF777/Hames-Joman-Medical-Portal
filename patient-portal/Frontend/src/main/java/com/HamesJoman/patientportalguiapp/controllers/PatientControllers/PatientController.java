package com.HamesJoman.patientportalguiapp.controllers.PatientControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Logic for Patient views
 * Like 90% of this is taken from the doctor controller I did so check that for more comments
 *
 * @author Collin Fair and Corey Suhr
 */
public class PatientController {

    @FXML private Label nextApptLabel;
    @FXML private Label totalApptsLabel;
    @FXML private ListView appointmentListView;
    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML
    private ComboBox<String> filterComboBox;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<String> appointmentList = FXCollections.observableArrayList();
    private JsonNode allAppointments;

    /**
     * Set welcome text to welcome the patient and set combobox with filters for viewing appointments
     */
    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome back, " + SessionManager.getInstance().getFullName() + "!");

        // Set up filter
        filterComboBox.setItems(FXCollections.observableArrayList(
                "Today", "This Week", "This Month", "All Time"
        ));
        filterComboBox.setValue("Today");
        filterComboBox.setOnAction(e -> applyFilter());

        // Update appointment info
        try {
            HttpResponse<String> response = ApiClient.getAppointmentsByPatient(SessionManager.getInstance().getUserId());

            if (response.statusCode() == 200) {
                allAppointments = mapper.readTree(response.body());

                if (allAppointments.isEmpty()) {
                    nextApptLabel.setText("No Upcoming Appointments");
                    totalApptsLabel.setText("0 Appointments");
                    return;
                }

                // Populate list based on selected filter
                applyFilter();
                updateNextAppointment();
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

    /**
     * Filter apts based off the users filter selection
     */
    private void applyFilter() {
        if (allAppointments == null) {
            return;
        }
        appointmentList.clear();

        LocalDate today = LocalDate.now();
        String filter = filterComboBox.getValue();

        // Set week range (Sunday-Saturday)
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<JsonNode> filteredList = new ArrayList<>();

        for (JsonNode apt : allAppointments) {
            LocalDate date = LocalDate.parse(apt.get("date").asText());

            boolean include = switch (filter) {
                case "Today" -> date.equals(today);
                case "This Week" -> !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
                case "This Month" -> date.getMonth() == today.getMonth() && date.getYear() == today.getYear();
                default -> true;
            };

            // If its in the filter, add it
            if (include) {
                filteredList.add(apt);
            }
        }

        // Sort filtered apts by date and their start time
        filteredList.sort(Comparator.comparing(a -> {
            LocalDate d = LocalDate.parse(a.get("date").asText());
            LocalTime t = LocalTime.parse(a.get("startTime").asText());
            return LocalDateTime.of(d, t);
        }));

        // Populate the view
        for (JsonNode apt: filteredList) {
            String startTime = apt.get("startTime").asText();
            String endTime = apt.get("endTime").asText();
            String status = apt.get("status").asText();
            int aptId = apt.get("id").asInt();

            JsonNode doctorNode = apt.get("doctor");
            String doctorInfo = (doctorNode == null || doctorNode.isNull()) ? "Deleted Doctor"
                    : "Dr. " + doctorNode.get("firstName").asText() + " " + doctorNode.get("lastName").asText() +
                    " (ID: " + doctorNode.get("id").asInt() + ")";

            appointmentList.add(String.format("ID: %d | %s | %s–%s | Doctor: %s | Status: %s",
                    aptId, apt.get("date").asText(), startTime, endTime, doctorInfo, status));
        }

        appointmentListView.setItems(appointmentList);
        totalApptsLabel.setText(appointmentList.size() + " Appointments");
    }

    /**
     * Update the next upcoming appointment
     */
    private void updateNextAppointment() {
        if (allAppointments == null || allAppointments.isEmpty()) {
            nextApptLabel.setText("No Upcoming Appointments");
            return;
        }

        // Set next appointment
        LocalDateTime now = LocalDateTime.now();
        JsonNode nextAppointment = null;
        LocalDateTime nextAppointmentTime = null;

        for (JsonNode apt : allAppointments) {
            LocalDate date = LocalDate.parse(apt.get("date").asText());
            LocalTime start = LocalTime.parse(apt.get("startTime").asText());
            LocalTime end = LocalTime.parse(apt.get("endTime").asText());

            LocalDateTime aptStart = LocalDateTime.of(date, start);
            LocalDateTime aptEnd = LocalDateTime.of(date, end);

            // Make sure its not showing past appointments
            if (aptEnd.isBefore(now)) {
                continue;
            }

            // Find the earliest apt coming up
            if (nextAppointment == null || aptStart.isBefore(nextAppointmentTime)) {
                nextAppointment = apt;
                nextAppointmentTime = aptStart;
            }
        }

        if (nextAppointment != null) {
            JsonNode doctorNode = nextAppointment.get("doctor");

            String doctorInfo = (doctorNode == null || doctorNode.isNull()) ? "Deleted Doctor" :
                    "Dr. " + doctorNode.get("firstName").asText() + " " + doctorNode.get("lastName").asText();

            nextApptLabel.setText(doctorInfo + " " + nextAppointment.get("startTime").asText() + " - " +
                    nextAppointment.get("endTime").asText() + " on " + nextAppointment.get("date").asText());
        }
        else {
            nextApptLabel.setText("No Upcoming Appointments");
        }
    }

    /**
     * Open window on change password click
     */
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

    /**
     * Logout on button click
     */
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

    /**
     * Loads popup for cancelling an appointment
     */
    @FXML
    public void onCancelAptButtonClick() {
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Patient/cancel-pat-appt-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 400, 450);
            Stage popup = new Stage();
            popup.setTitle("Cancel Appointment");
            popup.setScene(scene);
            popup.show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}