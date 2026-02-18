package edu.secourse.patientportalguiapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SearchApptByDoctorView {
    @FXML
    private Button backButton;

    @FXML
    private TextArea outputBox;

    @FXML
    private TextField doctorIdField;

    private String generateRandomAppointment(String doctorId) {
        return String.format(
                "%s: Doctor '%s', Patient '%s'",
                String.format("%d/%d/%d",
                        (int)(Math.random() * 12 + 1),
                        (int)(Math.random() * 31 + 1),
                        (int)(Math.random() * 10 + 2020)
                ),
                doctorId,
                (int)(Math.random() * 1000000)
        );
    }

    @FXML
    private void onBackButtonClick() {
        // Close the current window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSearchButtonClick() {
        if (doctorIdField.getText().isBlank()) {
            return;
        }

        int randomQuantity = (int)(Math.random() * 5 + 1);
        ArrayList<String> appts = new ArrayList<>();
        for (int i = 0; i < randomQuantity; i++) {
            appts.add(generateRandomAppointment(doctorIdField.getText()));
        }

        outputBox.setText(
                "Showing appointments for: " + doctorIdField.getText() + "\n" + String.join("\n", appts)
        );
    }
}
