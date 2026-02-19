package com.HamesJoman.patient_portal.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private LocalTime startTime;
    @Column(nullable = false)
    private LocalTime endTime;
    // Many appointments can belong to one patient
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    // Many appointments can belong to one doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    // Stores enum in database as ACTIVE or CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    // Automatically updated
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    // Required by JPA
    public Appointment() {

    }

    public Appointment(LocalDate date, LocalTime startTime, LocalTime endTime,
                       Patient patient, Doctor doctor) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.patient = patient;
        this.doctor = doctor;
        // All appointments start as ACTIVE when created
        this.status = Status.ACTIVE;
    }

    /**
     * Runs automatically before a new appointment is saved to the database.
     */
    @PrePersist
    private void onCreate() {
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Runs automatically before an existing appointment is updated in the database.
     */
    @PreUpdate
    private void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}