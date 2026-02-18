package edu.secourse.patientportalguiapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SearchApptByPatientController {
    @FXML
    private Button backButton;

    @FXML
    private TextArea outputBox;

    @FXML
    private TextField patientIdField;

    private String generateRandomAppointment(String patientId) {
        return String.format(
                "%s: Patient '%s', Doctor '%s'",
                String.format("%d/%d/%d",
                        (int)(Math.random() * 12 + 1),
                        (int)(Math.random() * 31 + 1),
                        (int)(Math.random() * 10 + 2020)
                ),
                patientId,
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
        if (patientIdField.getText().isBlank()) {
            return;
        }

        int randomQuantity = (int)(Math.random() * 5 + 1);
        ArrayList<String> appts = new ArrayList<>();
        for (int i = 0; i < randomQuantity; i++) {
            appts.add(generateRandomAppointment(patientIdField.getText()));
        }

        outputBox.setText(
                "Showing appointments for: " + patientIdField.getText() + "\n" + String.join("\n", appts)
        );
    }
}