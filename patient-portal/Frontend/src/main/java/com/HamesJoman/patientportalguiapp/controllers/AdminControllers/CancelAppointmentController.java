package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import com.HamesJoman.patientportalguiapp.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CancelAppointmentController {
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

    @FXML
    public void initialize(){
        appointmentSelectComboBox.getItems().addAll("Appointment");
        loadAppointments();
    }

    private void loadAppointments() {
        try{
            HttpResponse<String> response = ApiClient.getAllAppointments();

            if(response.statusCode() == 200){
                JsonNode appointments = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for(JsonNode appointment: appointments){
                    if(!appointment.get("status").asText().equalsIgnoreCase("CANCELLED")){
                        int id = appointment.get("id").asInt();
                        String patientName = appointment.get("patient").get("lastName").asText();
                        String doctorName = appointment.get("doctor").get("lastName").asText();
                        items.add(id + " - " + patientName + "/" + doctorName);
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

    public void onCancelButtonClick() {
        if(selectedAppointmentId == -1){
            actionText.setText("Please select an appointment");
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/HamesJoman/patientportalguiapp/Admin/cancel-appt-confirmation-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 200, 100);

            CancelAppointmentConfirmController confirmController = loader.getController();

            Stage popup = new Stage();
            popup.setTitle("Cancel Appointment");
            popup.setScene(scene);
            popup.showAndWait();

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

    public void onConfirmation(){

    }

    public void onAppointmentSelected() {
        String selected = appointmentSelectComboBox.getValue();
        if(selected == null){
            return;
        }

        int id = Integer.parseInt(selected.split(" - ")[0].trim());
        selectedAppointmentId = id;
    }
}
