package ru.kalita.ratelimit.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kalita.ratelimit.annotation.RateLimiting;

@RestController
@RequestMapping("/api")
public class ApiController {

    @RateLimiting
    @GetMapping("/limitedResource")
    public ResponseEntity<Void> getResponse() {
        return ResponseEntity.ok().build();
    }
}
