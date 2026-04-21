package com.HamesJoman.patientportalguiapp.controllers.PatientControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.HamesJoman.patientportalguiapp.SessionManager;
import com.HamesJoman.patientportalguiapp.controllers.SharedControllers.ConfirmController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * Controller that handles logic for cancelling appointment from patient dashboard
 *
 * @author Corey Suhr and Collin Fair
 */
public class CancelPatientAppointmentController {
    @FXML
    private Label actionText;

    @FXML
    private ComboBox<String> appointmentSelectComboBox;

    @FXML
    private Label detailDoctorLabel;

    @FXML
    private Label detailDateLabel;

    @FXML
    private Label detailTimeLabel;

    @FXML
    private Label detailStatusLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;

    private final ObjectMapper mapper = new ObjectMapper();

    private int selectedAppointmentId = -1;

    // This is how we are going to refresh the dashboards, this can be called to refresh appointments on success
    private Runnable onCancelSuccess;

    /**
     * On a successful cancel, mark it as such
     *
     * @param callback code to run on cancel success
     */
    public void setOnCancelSuccess (Runnable callback) {
        this.onCancelSuccess = callback;
    }

    /**
     * Initialize the appointment combo box and clear details
     */
    @FXML
    public void initialize(){
       clearDetails();
        loadAppointments();
    }

    /**
     * Populate appointments in appointmentSelectComboBox
     */
    private void loadAppointments() {
        try{
            HttpResponse<String> response = ApiClient.getAppointmentsByPatient(SessionManager.getInstance().getUserId());

            if(response.statusCode() == 200){
                JsonNode appointments = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for(JsonNode appointment: appointments){
                    if(appointment.get("status").asText().equalsIgnoreCase("ACTIVE")){
                        int id = appointment.get("id").asInt();
                        JsonNode doctorNode = appointment.get("doctor");
                        String doctorName = (doctorNode == null || doctorNode.isNull())
                                ? "Deleted Doctor"
                                : "Dr. " + doctorNode.get("firstName").asText() + " " + doctorNode.get("lastName").asText();

                        items.add("Appointment #" + id + " — " + doctorName);
                    }
                }

                appointmentSelectComboBox.setItems(items);

                if (items.isEmpty()) {
                    actionText.setText("You have no active appointments");
                }
            }
            else{
                actionText.setText("Failed to load appointments: " + response.statusCode());
            }
        } catch (Exception e){
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    public void onBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Logic for cancelling an appointment
     */
    public void onCancelButtonClick() {
        // Ensures an appointment is selected
        if(selectedAppointmentId == -1){
            actionText.setText("Please select an appointment");
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/confirmation-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 200, 100);

            // Gets controller class for confirmation view
            ConfirmController confirmController = loader.getController();

            Stage popup = new Stage();
            popup.setTitle("Confirmation");
            popup.setScene(scene);
            // Show view and wait for it to close
            popup.showAndWait();

            // Once view is closed, check if the cancellation is confirmed, and attempt to cancel appointment if it is
            if(confirmController.isConfirmed()){
                try {
                    HttpResponse<String> response = ApiClient.cancelAppointment(selectedAppointmentId);
                    if (response.statusCode() == 200) {
                        actionText.setText("Appointment #" + selectedAppointmentId + " cancelled.");
                        selectedAppointmentId = -1;
                        clearDetails();
                        loadAppointments();

                        // Notify the dashboard to refresh
                        if (onCancelSuccess != null) {
                            onCancelSuccess.run();
                        }
                    } else {
                        actionText.setText("Failed to cancel: " + response.statusCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Update selectedAppointmentId once an appointment is selected
     */
    public void onAppointmentSelected() {
        String selected = appointmentSelectComboBox.getValue();
        if(selected == null){
            return;
        }

        int id = Integer.parseInt(selected.split("#")[1].split("\\s")[0].trim());
        selectedAppointmentId = id;

        try {
            HttpResponse<String> response = ApiClient.getAppointmentById(String.valueOf(id));

            if (response.statusCode() == 200) {
                JsonNode apt = mapper.readTree(response.body());

                JsonNode doctorNode = apt.get("doctor");
                String doctorInfo = (doctorNode == null || doctorNode.isNull()) ? "Deleted Doctor"
                        : "Dr. " + doctorNode.get("firstName").asText() + " " + doctorNode.get("lastName").asText()
                        + " (ID: " + doctorNode.get("id").asInt() + ")";

                actionText.setText("Review the details below before cancelling the appointment");
                detailDoctorLabel.setText(doctorInfo);
                detailDateLabel.setText(apt.get("date").asText());
                detailTimeLabel.setText(apt.get("startTime").asText() + " – " + apt.get("endTime").asText());
                detailStatusLabel.setText(apt.get("status").asText());
            }
            else {
                actionText.setText("Failed to load appointment");
            }

        } catch(Exception e) {
            actionText.setText("Couldnt connect to server");
            e.printStackTrace();
        }
    }

    /**
     * Clears the detail panel back to placeholder
     */
    public void clearDetails() {
        detailDoctorLabel.setText("—");
        detailDateLabel.setText("—");
        detailTimeLabel.setText("—");
        detailStatusLabel.setText("—");
    }
}
