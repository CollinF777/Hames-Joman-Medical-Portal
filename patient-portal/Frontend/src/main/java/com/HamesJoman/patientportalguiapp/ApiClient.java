package com.HamesJoman.patientportalguiapp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class to handle all HTTP communication between
 * the JavaFX front end and RESTful APIs in the backend
 * @author Collin Fair, Liam Callahan
 */
public class ApiClient {
    // Base backend API url
    private static final String BASE_URL = "http://localhost:8080/api";

    // Start the HttpClient instance
    private static final HttpClient client = HttpClient.newHttpClient();

    //===========================================================================
    // Auth Routes
    //===========================================================================
    /**
     * Send a login request
     *
     * @param username Users username
     * @param password Users password
     * @return HttpResponse containing String response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> login(String username, String password) throws Exception {
        /**
         * JSON Request body
         * Should end up looking like
         * {"username": "some username", "password": "some password"}
         * Backquotes are there cuz otherwise you cant have the ""
         */
        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        // Make POST request for /auth/login
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Send out the request and get a String response
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    //===========================================================================
    // User Routes
    //===========================================================================
    /**
     * Fetch all users
     * GET /api/users
     *
     * @return HttpResponse containing String response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> getAllUsers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Fetch a single user by ID
     * GET /api/users/{id}
     *
     * @param id User id
     * @return HttpResponse containing String response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> getUserById(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + id))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     *
     * @param firstName User first name
     * @param lastName User last name
     * @param username users username
     * @param password users password
     * @param role users role
     * @return HttpResponse containing String response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> createUser(String firstName, String lastName,
                                                  String username, String password, String role) throws Exception {
        /**
         * JSON Request body
         * Should look like
         * {"firstName": "name", "lastName": "name", "username": "name", "password": "hashedpass", "role": "userRole"}
         */
        String json = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                firstName, lastName, username, password, role
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Update an existing user
     * PUT /api/users/{id}
     *
     * @param id user id
     * @param firstName user first name
     * @param lastName user last name
     * @param username users username
     * @param password users hashed password
     * @param role users role
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> updateUser(String id, String firstName, String lastName,
                                                  String username, String password, String role) throws Exception {
        /**
         * JSON Request body
         * Should look like
         * {"firstName": "name", "lastName": "name", "username": "name", "password": "hashedpass", "role": "userRole"}
         */
        String json = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                firstName, lastName, username, password, role
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Change the logged in users password
     * PUT /api/users/{id}/change-password
     *
     * @param id The users ID from the session
     * @param currentPassword The users entered current password
     * @param newPassword The users new password
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> changePassword(int id, String currentPassword,
                                                      String newPassword) throws Exception {
        /**
         * JSON request body
         * Should look like
         * {"currentPassword": "some input", "newPassword": "some input"}
         */
        String json = String.format("{\"currentPassword\": \"%s\", \"newPassword\":\"%s\"}", currentPassword, newPassword);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + id + "/change-password"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Delete an existing user
     * PUT /api/users/{id}
     *
     * @param id user id
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> deleteUser(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + id))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    //===========================================================================
    // Appointment Routes
    //===========================================================================

    /**
     * Fetch all appointments in the db
     * GET /api/appointments
     *
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> getAllAppointments() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Fetch a single appointment by ID
     * GET /api/appointments/{id}
     *
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> getAppointmentById(String id) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments/" + id))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Get all appointments for a given patient
     * GET /api/appointments/patient/{patientId}
     *
     * @param patientId the patients id
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> getAppointmentsByPatient(int patientId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments/patient/" + patientId))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Get all appointments for a given doctor
     * GET /api/appointments/doctor/{doctorId}
     *
     * @param doctorId the doctors id
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> getAppointmentsByDoctor(int doctorId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments/doctor/" + doctorId))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Creates a new appointment
     * POST /api/appointments
     *
     * @param date Tbe date of the appointment YYYY-MM-DD
     * @param startTime The appointments start time HH:mm
     * @param endTime The appointments end time HH:mm
     * @param patientId The appointments patient id
     * @param doctorId The appointments doctors id
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> createAppointment(String date, String startTime, String endTime,
                                                         int patientId, int doctorId) throws Exception {
        /**
         * JSON Request Body
         * should look like
         * {"date": "YYYY-MM-DD", "startTime": "HH:mm", "endTime": "HH:mm", "patientId": some int, "doctorId": some int}
         */
        String json = String.format("{\"date\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\",\"patientId\":%d,\"doctorId\":%d}",
                date, startTime, endTime, patientId, doctorId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Update an existing appointment
     * PUT /api/appointments/{id}
     *
     * @param id The appointment id
     * @param date The appointment date
     * @param startTime The appointments start time
     * @param endTime The appointments end time
     * @param patientId The appointments patient id
     * @param doctorId The appointments doctor id
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> updateAppointment(int id, String date, String startTime,
                                                         String endTime, int patientId, int doctorId) throws Exception {
        String json = String.format("{\"date\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\",\"patientId\":%d,\"doctorId\":%d}",
                date, startTime, endTime, patientId, doctorId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Cancel an existing appointment
     * PUT /api/appointments/{id}/cancel
     *
     * @param id The appointment id
     * @return HttpResponse containing String Response
     * @throws Exception if request fails
     */
    public static HttpResponse<String> cancelAppointment(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/appointments/" + id + "/cancel"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
