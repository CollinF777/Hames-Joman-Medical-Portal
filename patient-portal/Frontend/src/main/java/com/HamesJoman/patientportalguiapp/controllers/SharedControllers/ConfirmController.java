package com.HamesJoman.patientportalguiapp.controllers.SharedControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for confirmation of a separate window
 *
 * @author Corey Suhr
 */
public class ConfirmController {
    @FXML
    private Button noButton;

    @FXML
    private Button yesButton;

    private Boolean confirmed = false;

    public Boolean isConfirmed(){
        return confirmed;
    }

    public void onYesButtonClick() {
        confirmed = true;

        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }

    public void onNoButtonClick() {
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }
}
