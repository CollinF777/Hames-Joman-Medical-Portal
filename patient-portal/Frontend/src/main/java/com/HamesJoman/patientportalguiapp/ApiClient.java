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
}
