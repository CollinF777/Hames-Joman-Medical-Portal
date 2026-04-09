package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for updating appointment
 *
 * @author Corey Suhr
 */
public class UpdateAppointmentController {
    @FXML
    private Label actionText;

    @FXML
    private ComboBox appointmentSelectComboBox;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private Spinner<Integer> startHourSpinner;

    @FXML
    private Spinner<Integer> startMinuteSpinner;

    @FXML
    private Spinner<Integer> endHourSpinner;

    @FXML
    private Spinner<Integer> endMinuteSpinner;

    @FXML
    private ComboBox<String> patientComboBox;

    @FXML
    private ComboBox<String> doctorComboBox;

    @FXML
    private Button backButton;

    @FXML
    private Button updateButton;

    private final ObjectMapper mapper = new ObjectMapper();

    private int selectedAppointmentId = -1;

    /**
     * Initialize the spinners for 15 minute intervals and load appointments
     */
    @FXML
    public void initialize(){
        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));

        List<Integer> minutes = List.of(0, 15, 30, 45);

        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList(minutes)));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(FXCollections.observableArrayList(minutes)));

        appointmentSelectComboBox.getItems().addAll("Appointment");
        patientComboBox.getItems().addAll("Patient");
        doctorComboBox.getItems().addAll("Doctor");
        setEditFieldsDisabled(true);
        loadAppointments();

        appointmentSelectComboBox.setOnAction(e -> onAppointmentSelected());
    }

    /**
     * Method for filling out existing fields based on selected appointment
     */
    private void onAppointmentSelected() {
        String selected = appointmentSelectComboBox.getValue().toString();
        if(selected == null){
            return;
        }

        int id = Integer.parseInt(selected.split(" - ")[0].trim());
        selectedAppointmentId = id;

        try{
            HttpResponse<String> response = ApiClient.getAppointmentById(String.valueOf(id));

            if(response.statusCode() == 200){
                JsonNode json = mapper.readTree(response.body());
                appointmentDatePicker.setValue(LocalDate.parse(json.get("date").asText()));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime startTime = LocalTime.parse(json.get("startTime").asText(), formatter);
                LocalTime endTime = LocalTime.parse(json.get("endTime").asText(), formatter);

                startHourSpinner.getValueFactory().setValue(startTime.getHour());
                startMinuteSpinner.getValueFactory().setValue(startTime.getMinute());
                endHourSpinner.getValueFactory().setValue(endTime.getHour());
                endMinuteSpinner.getValueFactory().setValue(endTime.getMinute());

                loadPatientsDoctors();

                setEditFieldsDisabled(false);
                actionText.setText("Editing appointment " + id + ".");
            }
        } catch(Exception e){
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    /**
     * Method for loading all patients and doctors
     */
    private void loadPatientsDoctors() {
        try{
            HttpResponse<String> response = ApiClient.getAllUsers();

            if(response.statusCode() == 200){
                JsonNode users = mapper.readTree(response.body());
                ObservableList<String> patientItems = FXCollections.observableArrayList();
                ObservableList<String> doctorItems = FXCollections.observableArrayList();

                HttpResponse<String> apptResponse = ApiClient.getAppointmentById("" + selectedAppointmentId);
                JsonNode appointment = mapper.readTree(apptResponse.body());
                int currentPatientId = appointment.get("patient").get("id").asInt();
                int currentDoctorId = appointment.get("doctor").get("id").asInt();

                String currentPatient = "";
                String currentDoctor = "";

                for(JsonNode user: users){
                    int id = user.get("id").asInt();
                    String name = user.get("firstName").asText() + " " + user.get("lastName").asText();
                    String role = user.get("role").asText();

                    if(role.equals("Patient")){
                        patientItems.add(id + " - " + name);

                        if(currentPatientId == id){
                            currentPatient = id + " - " + name;
                        }
                    }
                    else if(role.equals("Doctor")){
                        doctorItems.add(id + " - " + name);

                        if(currentDoctorId == id){
                            currentDoctor = id + " - " + name;
                        }
                    }
                }

                patientComboBox.setItems(patientItems);
                doctorComboBox.setItems(doctorItems);

                patientComboBox.setValue(currentPatient);
                doctorComboBox.setValue(currentDoctor);
            }
            else{
                actionText.setText("Failed to load users: " + response.statusCode());
            }
        } catch (Exception e){
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }

    /**
     * Method for loading all existing appointments
     */
    private void loadAppointments() {
        try{
            HttpResponse<String> response = ApiClient.getAllAppointments();

            if(response.statusCode() == 200){
                JsonNode appointments = mapper.readTree(response.body());
                ObservableList<String> items = FXCollections.observableArrayList();

                for(JsonNode appointment: appointments){
                    int id = appointment.get("id").asInt();
                    String patientName = appointment.get("patient").get("lastName").asText();
                    String doctorName = appointment.get("doctor").get("lastName").asText();
                    items.add(id + " - " + patientName + "/" + doctorName);
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

    /**
     * Method for disabling all edit fields if needed
     *
     * @param disabled boolean that says if fields are editable or not
     */
    private void setEditFieldsDisabled(boolean disabled){
        appointmentDatePicker.setDisable(disabled);
        startHourSpinner.setDisable(disabled);
        startMinuteSpinner.setDisable(disabled);
        endHourSpinner.setDisable(disabled);
        endMinuteSpinner.setDisable(disabled);
        patientComboBox.setDisable(disabled);
        doctorComboBox.setDisable(disabled);
        updateButton.setDisable(disabled);
    }

    /**
     * Goes back to default admin view on back button click
     */
    @FXML
    private void onBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Updates appointment if all fields are correct and there are no conflicts
     */
    @FXML
    private void onUpdateButtonClick() {
        if(selectedAppointmentId == -1){
            actionText.setText("Please select an appointment");
            return;
        }
        if(appointmentDatePicker.getValue() == null ||
                patientComboBox.getValue() == null ||
                doctorComboBox.getValue() == null){
            actionText.setText("Please fill in all fields");
            return;
        }

        int patientId = Integer.parseInt(patientComboBox.getValue().split(" - ")[0].trim());
        int doctorId = Integer.parseInt(doctorComboBox.getValue().split(" - ")[0].trim());

        String date = appointmentDatePicker.getValue().toString();

        LocalTime startTime = LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue());
        LocalTime endTime = LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue());

        if(!endTime.isAfter(startTime)){
            actionText.setText("End time must be after start time.");
            return;
        }

        String startStr = String.format("%02d:%02d", startTime.getHour(), startTime.getMinute());
        String endStr   = String.format("%02d:%02d", endTime.getHour(),   endTime.getMinute());

        try{
            HttpResponse<String> response = ApiClient.updateAppointment(
                    selectedAppointmentId, date, startStr, endStr, patientId, doctorId
            );

            if(response.statusCode() == 200){
                actionText.setText("Appointment successfully updated");
                loadAppointments();
                setEditFieldsDisabled(true);
            }
            else{
                actionText.setText("Update failed on appointment: " + response.statusCode());
            }
        } catch (Exception e){
            actionText.setText("Couldn't connect to server");
            e.printStackTrace();
        }
    }
}
