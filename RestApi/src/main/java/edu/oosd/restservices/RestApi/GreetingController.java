package edu.oosd.restservices.RestApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// New imports
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private static final String template_home = "Hello! You are at %s";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public ResponseEntity<Greeting> greeting(@RequestParam(defaultValue = "World") String name) {

        Greeting greeting = new Greeting(
                counter.incrementAndGet(),
                String.format(template, name));

        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        String body = """
            {
              "message": "Welcome to the REST API",
              "endpoints": {
                "greeting": "http://localhost:8080/greeting"
              }
            }
            """;

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
