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

public class CancelAppointmentConfirmController {
    @FXML
    private Button noButton;

    @FXML
    private Button yesButton;

    private Boolean confirmed = false;

    public Boolean isConfirmed(){
        return confirmed;
    }

    public void onYesButtonClick(){
        confirmed = true;

        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }

    public void onNoButtonClick() {
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }
}
