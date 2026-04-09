package com.HamesJoman.patientportalguiapp.controllers.AdminControllers;

import com.HamesJoman.patientportalguiapp.ApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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
 * Controller for the admin view all appointments screen with time filtering
 *
 * @author Mohamed Musa
 */
public class ViewAppointmentsController {

    @FXML private ComboBox<String> filterComboBox;
    @FXML private ListView<String> appointmentListView;
    @FXML private Label totalApptsLabel;

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObservableList<String> appointmentList = FXCollections.observableArrayList();
    private JsonNode allAppointments;

    /**
     * Initialize combo box with filter options and fetch appointments
     */
    @FXML
    public void initialize() {
        filterComboBox.setItems(FXCollections.observableArrayList(
                "Today", "This Week", "This Month", "All Time"
        ));
        filterComboBox.setValue("All Time");
        filterComboBox.setOnAction(e -> applyFilter());

        try {
            HttpResponse<String> response = ApiClient.getAllAppointments();
            if (response.statusCode() == 200) {
                allAppointments = mapper.readTree(response.body());
                applyFilter();
            } else {
                appointmentList.add("Failed to fetch appointments: " + response.statusCode());
                appointmentListView.setItems(appointmentList);
            }
        } catch (Exception e) {
            appointmentList.add("Couldn't connect to server");
            appointmentListView.setItems(appointmentList);
            e.printStackTrace();
        }
    }

    /**
     * Apply the filter so the user sees the correct appointments
     */
    private void applyFilter() {
        if (allAppointments == null) return;
        appointmentList.clear();

        LocalDate today = LocalDate.now();
        String filter = filterComboBox.getValue();

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<JsonNode> filtered = new ArrayList<>();

        for (JsonNode apt : allAppointments) {
            LocalDate date = LocalDate.parse(apt.get("date").asText());
            boolean include = switch (filter) {
                case "Today" -> date.equals(today);
                case "This Week" -> !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
                case "This Month" -> date.getMonth() == today.getMonth() && date.getYear() == today.getYear();
                default -> true;
            };
            if (include) filtered.add(apt);
        }

        filtered.sort(Comparator.comparing(a -> {
            LocalDate d = LocalDate.parse(a.get("date").asText());
            LocalTime t = LocalTime.parse(a.get("startTime").asText());
            return LocalDateTime.of(d, t);
        }));

        for (JsonNode apt : filtered) {
            int aptId = apt.get("id").asInt();
            String date = apt.get("date").asText();
            String startTime = apt.get("startTime").asText();
            String endTime = apt.get("endTime").asText();
            String status = apt.get("status").asText();

            JsonNode patientNode = apt.get("patient");
            String patientInfo = (patientNode == null || patientNode.isNull()) ? "Deleted Patient"
                    : patientNode.get("firstName").asText() + " " + patientNode.get("lastName").asText()
                    + " (ID: " + patientNode.get("id").asInt() + ")";

            JsonNode doctorNode = apt.get("doctor");
            String doctorInfo = (doctorNode == null || doctorNode.isNull()) ? "Deleted Doctor"
                    : "Dr. " + doctorNode.get("firstName").asText() + " " + doctorNode.get("lastName").asText()
                    + " (ID: " + doctorNode.get("id").asInt() + ")";

            appointmentList.add(String.format("ID: %d | %s | %s–%s | Patient: %s | Doctor: %s | Status: %s",
                    aptId, date, startTime, endTime, patientInfo, doctorInfo, status));
        }

        appointmentListView.setItems(appointmentList);
        totalApptsLabel.setText(filtered.size() + " Appointments");
    }
}