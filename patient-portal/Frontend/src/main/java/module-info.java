module edu.secourse.patientportalguiapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens edu.secourse.patientportalguiapp to javafx.fxml;
    exports edu.secourse.patientportalguiapp;

    opens edu.secourse.patientportalguiapp.controllers to javafx.fxml;
    exports edu.secourse.patientportalguiapp.controllers;
}