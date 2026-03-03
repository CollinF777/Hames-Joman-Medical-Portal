package com.HamesJoman.patientportalguiapp.controllers.DoctorControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class DoctorController {

    @FXML
    private StackPane contentStack;

    @FXML
    private GridPane dashboardPane;

    @FXML
    private GridPane appointmentsPane;

    @FXML
    private GridPane passwordPane;

    @FXML
    public void initialize() {
        // Initialization logic if required
    }

    @FXML
    public void showDashboard(ActionEvent event) {
        dashboardPane.setVisible(true);
        dashboardPane.setManaged(true);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    public void showAppointments(ActionEvent event) {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(true);
        appointmentsPane.setManaged(true);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    public void showPassword(ActionEvent event) {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(true);
        passwordPane.setManaged(true);
    }
}
