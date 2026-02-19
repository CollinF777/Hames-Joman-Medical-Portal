module edu.secourse.patientportalguiapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.HamesJoman.patientportalguiapp to javafx.fxml;
    exports com.HamesJoman.patientportalguiapp;

    opens com.HamesJoman.patientportalguiapp.controllers to javafx.fxml;
    exports com.HamesJoman.patientportalguiapp.controllers;
    exports com.HamesJoman.patientportalguiapp.controllers.AdminControllers;
    opens com.HamesJoman.patientportalguiapp.controllers.AdminControllers to javafx.fxml;
}