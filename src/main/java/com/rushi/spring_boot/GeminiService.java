package com.rushi.spring_boot;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class GeminiService {

    @Value("${gemini_api_url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String chatWithGemini(String userMessage) {
        // ✅ Correctly create request body
        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", userMessage))
            ))
        );

        // ✅ Use correct Spring HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // ✅ Call Gemini API
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            // ✅ Check for null or unexpected response structure
            if (response.getBody() == null || !response.getBody().containsKey("candidates")) {
                return "No valid response from Gemini API.";
            }

            // ✅ Extract Gemini response
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates.isEmpty()) {
                return "No candidates found in the response.";
            }

            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts.isEmpty()) {
                return "No content parts found in the response.";
            }
            Map<String, Object> part = parts.get(0);

            return (String) part.get("text");

        } catch (HttpClientErrorException e) {
            // Handle client errors (e.g., 4xx)
            return "Error in request: " + e.getMessage();
        } catch (Exception e) {
            // Handle other unexpected errors
            return "Error: " + e.getMessage();
        }
    }
}
