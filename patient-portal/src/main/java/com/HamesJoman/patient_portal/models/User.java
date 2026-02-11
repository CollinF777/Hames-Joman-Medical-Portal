package com.HamesJoman.patient_portal.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * Model for User.
 *
 * @author Collin Fair
 */
@Entity // Marks as DB table
@Table(name = "users") // Guess.
// Puts all users into one table instead of diff tables for diff roles
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Creates the correct object based on the user-type aka their role
@DiscriminatorColumn(name = "user-type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id // PK
    // Makes use of SQL Auto-Increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // I'm not gonna comment each of these, names are self-explanatory just msg me if confused
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password; // hashed string (idk how we're gonna hash yet, look into jwt integration)
    @Column(nullable = false)
    private String role; // Slight change from how we did it in 427, ask me for clarification if curious
    private LocalDateTime lastLogin;
    private LocalDateTime lastPasswordChange;

    // You need a default constructor or else JPA takes you out back and shoots you
    protected User() {

    }

    public User(int id, String firstName, String lastName, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.lastPasswordChange = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastPasswordChange = LocalDateTime.now();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }
}