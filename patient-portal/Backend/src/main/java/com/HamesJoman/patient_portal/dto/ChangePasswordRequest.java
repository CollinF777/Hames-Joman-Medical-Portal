package com.HamesJoman.patient_portal.dto;

/**
 * DTO for whenever a user wants to change their password
 * Contains their current pw and old pw
 *
 * @author Collin Fair
 */
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
