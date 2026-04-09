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
 * @author Corey Suhr
 */
public class CancelPatientAppointmentController {
    @FXML
    private Label actionText;

    @FXML
    private ComboBox<String> appointmentSelectComboBox;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;

    private final ObjectMapper mapper = new ObjectMapper();

    private int selectedAppointmentId = -1;

    /**
     * Initialize the appointment combo box with all appointments for the patient
     */
    @FXML
    public void initialize(){
        appointmentSelectComboBox.getItems().addAll("Appointment");
        loadAppointments();
    }

    /**
     * Populate appointments in appointmentSelectComboBox
     */
    private void loadAppointments() {
        try{
            HttpResponse<String> response = ApiClient.getAllAppointments();

            if(response.statusCode() == 200){
                JsonNode appointments = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for(JsonNode appointment: appointments){
                    if(
                            !appointment.get("status").asText().equalsIgnoreCase("CANCELLED")
                                    && appointment.get("patient").get("id").asInt() == SessionManager.getInstance().getUserId()
                    ){
                        int id = appointment.get("id").asInt();
                        String fName = appointment.get("doctor").get("firstName").asText();
                        String lName = appointment.get("doctor").get("lastName").asText();
                        items.add(id + " - " + fName + " " + lName);
                    }
                }

                appointmentSelectComboBox.setItems(items);
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
                    ApiClient.cancelAppointment(selectedAppointmentId);
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

        int id = Integer.parseInt(selected.split(" - ")[0].trim());
        selectedAppointmentId = id;
    }
}
