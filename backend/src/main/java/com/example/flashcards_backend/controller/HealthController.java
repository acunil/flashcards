package com.example.flashcards_backend.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(
    name = "Health Controller",
    description = "Endpoint for checking the health status of the application.")
@RestController
public class HealthController {

  @ApiResponse(responseCode = "200", description = "Health check successful")
  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> healthCheck() {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "ok");
    response.put("uptime", System.currentTimeMillis());
    return ResponseEntity.ok(response);
  }
}
