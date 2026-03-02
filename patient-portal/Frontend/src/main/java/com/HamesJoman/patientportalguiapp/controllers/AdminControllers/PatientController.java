package your.package;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class PatientController {

    @FXML
    private GridPane dashboardPane;

    @FXML
    private GridPane appointmentsPane;

    @FXML
    private GridPane passwordPane;

    @FXML
    private void showDashboard() {
        dashboardPane.setVisible(true);
        dashboardPane.setManaged(true);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    private void showAppointments() {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(true);
        appointmentsPane.setManaged(true);

        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    @FXML
    private void showPassword() {
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);

        appointmentsPane.setVisible(false);
        appointmentsPane.setManaged(false);

        passwordPane.setVisible(true);
        passwordPane.setManaged(true);
    }
}