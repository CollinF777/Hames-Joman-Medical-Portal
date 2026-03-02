package com.HamesJoman.patient_portal.dto;

/**
 * Data Transfer Object for appointment creation and updates.
 * Date and time fields are received as strings and parsed in the service layer.
 *
 * Expected formats:
 *   date      — "YYYY-MM-DD"  (e.g. "2025-08-15")
 *   startTime — "HH:mm"       (e.g. "09:00")
 *   endTime   — "HH:mm"       (e.g. "09:30")
 *
 * @author Nathan Amidon
 */
public class AppointmentRequest {

    private String date;
    private String startTime;
    private String endTime;
    private int patientId;
    private int doctorId;

    // Getters and setters required for JSON serialization/deserialization

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}
