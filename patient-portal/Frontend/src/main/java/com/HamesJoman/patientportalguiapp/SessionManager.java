package com.HamesJoman.patientportalguiapp;

/**
 * Session manager to hold user data for whoever is logged in
 * Works as a singleton for getting active users info
 * Clear on logout
 *
 * @author Collin Fair
 */
public class SessionManager {
    private static SessionManager instance;

    /**
     * Private constructor to make this a singleton
     * Basically we arent letting any other class make a session
     */
    private SessionManager() {

    }

    // This is how you're gonna get the session instance, this used for the whole application
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String role;

    /**
     * Fills up the session with data when a user logins in
     *
     * @param userId users db id
     * @param firstName users first name
     * @param lastName users last name
     * @param username users username
     * @param role users role
     */
    public void setUser(int userId, String firstName, String lastName,
                         String username, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.role = role;
    }

    /**
     * This will clear any and all data
     * DO THIS on every logout, if you see a logout not doing this then add it
     */
    public void clear() {
        this.userId = 0;
        this.firstName = null;
        this.lastName = null;
        this.username = null;
        this.role = null;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isLoggedIn() {
        return userId != 0;
    }
}
