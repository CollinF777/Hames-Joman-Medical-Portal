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

    /**
     * Simple method to check if user confirmed or not
     *
     * @return True or False based on confirmed status
     */
    public Boolean isConfirmed(){
        return confirmed;
    }

    /**
     * On a yes button click change confirmed and open new window
     */
    public void onYesButtonClick() {
        confirmed = true;

        Stage stage = (Stage) yesButton.getScene().getWindow();
        stage.close();
    }

    /**
     * On a no button click
     */
    public void onNoButtonClick() {
        Stage stage = (Stage) noButton.getScene().getWindow();
        stage.close();
    }
}
