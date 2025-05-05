package com.rushi.spring_boot;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Controller
@RequestMapping("/api")
public class GeminiController {

    private final GeminiService service;

  
    @Autowired
    public GeminiController(GeminiService service) {
        this.service = service;
    }

    // Serve the frontend HTML page
    @GetMapping("/")
    public String home() {
        return "index";  // This will render index.html located in /static/
    }

    // Handle chat requests and return the response as JSON
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        String reply = service.chatWithGemini(message);

        // Return the response in JSON format
        Map<String, String> responseMap = Map.of("response", reply);
        return ResponseEntity.ok(responseMap);
    }
    
    // Health Check Endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthStatus = Map.of(
            "status", "UP",
            "application", "Gemini Chatbot",
            "message", "Application is running smoothly",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(healthStatus);
    }
}
