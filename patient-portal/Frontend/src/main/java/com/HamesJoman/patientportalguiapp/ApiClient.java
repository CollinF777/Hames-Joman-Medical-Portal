package com.HamesJoman.patientportalguiapp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class to handle all HTTP communication between
 * the JavaFX front end and RESTful APIs in the backend
 */
public class ApiClient {
    // Base backend API url
    private static final String BASE_URL = "http://localhost:8080/api";

    // Start the HttpClient instance
    private static final HttpClient client = HttpClient.newHttpClient();

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
}
