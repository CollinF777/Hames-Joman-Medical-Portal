module com.HamesJoman.patientportalguiapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;

    requires com.fasterxml.jackson.databind;

    opens com.HamesJoman.patientportalguiapp to javafx.fxml;
    exports com.HamesJoman.patientportalguiapp;

    opens com.HamesJoman.patientportalguiapp.controllers to javafx.fxml;
    exports com.HamesJoman.patientportalguiapp.controllers.AdminControllers;
    opens com.HamesJoman.patientportalguiapp.controllers.AdminControllers to javafx.fxml;
    exports com.HamesJoman.patientportalguiapp.controllers.PatientControllers;
    opens com.HamesJoman.patientportalguiapp.controllers.PatientControllers to javafx.fxml;
}