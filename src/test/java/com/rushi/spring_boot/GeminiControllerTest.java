package com.rushi.spring_boot;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GeminiController.class)
public class GeminiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private GeminiService geminiService;

    @Test
    public void testHomeEndpoint() throws Exception {
        mockMvc.perform(get("/api/"))
               .andExpect(status().isOk())
               .andExpect(view().name("index"));
    }

    @Test
    public void testChatEndpointSuccess() throws Exception {
        String inputMessage = "Hello Gemini!";
        String expectedReply = "Hi! How can I assist you today?";

        Mockito.when(geminiService.chatWithGemini(inputMessage)).thenReturn(expectedReply);

        String requestBody = "{ \"message\": \"" + inputMessage + "\" }";

        mockMvc.perform(post("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.response", is(expectedReply)));
    }

    @Test
    public void testChatEndpointWithEmptyMessage() throws Exception {
        String requestBody = "{ \"message\": \"\" }";
        String expectedReply = "Please enter a valid message.";

        Mockito.when(geminiService.chatWithGemini("")).thenReturn(expectedReply);

        mockMvc.perform(post("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.response", is(expectedReply)));
    }

    @Test
    public void testChatEndpointMissingMessageKey() throws Exception {
        String requestBody = "{}";
        String expectedReply = "Please enter a valid message.";

        // Assume service is called with `null` if key is missing
        Mockito.when(geminiService.chatWithGemini(null)).thenReturn(expectedReply);

        mockMvc.perform(post("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.response", is(expectedReply)));
    }
}
