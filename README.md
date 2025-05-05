#Spring Chatbot


This project is a simple AI chatbot using **Google Gemini API**, built with **Spring Boot (Java)** as the backend and **HTML/CSS/JS** as the frontend. It is deployed on **AWS Elastic Beanstalk (EBS)**.

---

## Features

- REST API backend with Spring Boot
- Frontend in HTML, CSS, and JavaScript
- Gemini (Google Generative AI) integration
- CORS-enabled communication
- Deployed on AWS EBS

---

## Technologies Used

- Java 17
- Spring Boot
- Maven
- Google Gemini API
- HTML, CSS, JavaScript
- AWS Elastic Beanstalk

---

## 1. Clone the Repository

```bash
git clone https://github.com/your-username/gemini-chatbot.git
```
```bash
cd gemini-chatbot

```

## 2. Backend Setup (Spring Boot)

Project Structure

```
src/
 └── main/
     ├── java/com/example/geminichatbot/
     │    ├── controller/ChatController.java
     │    ├── service/GeminiService.java
     │    └── model/ChatRequest.java
     └── resources/application.properties
```
`application.properties`
```properties
server.port=8080
gemini.api.key=${GEMINI_API_KEY}
spring.main.allow-bean-definition-overriding=true
```

`ChatRequest.java`

```Java 
package com.example.geminichatbot.model;

public class ChatRequest {
    private String message;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

```

`ChatController.java`
```Java
package com.example.geminichatbot.controller;

import com.example.geminichatbot.model.ChatRequest;
import com.example.geminichatbot.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String reply = geminiService.askGemini(request.getMessage());
        return ResponseEntity.ok(reply);
    }
}
```


`GeminiService.java`
```Java
package com.example.geminichatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent");

    public String askGemini(String message) {
        String requestBody = "{ \"contents\": [{\"parts\": [{\"text\": \"" + message + "\"}]}] }";

        return webClient.post()
                .uri("?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    // Extract answer from response JSON
                    int index = response.indexOf("text\":\"");
                    if (index != -1) {
                        int endIndex = response.indexOf("\"", index + 7);
                        return response.substring(index + 7, endIndex).replace("\\n", "\n");
                    }
                    return "No response from Gemini.";
                })
                .block();
    }
}
```

---

## 3. Frontend Setup (HTML/CSS/JS)

`index.html`
```HTML
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Gemini Chatbot</title>
  <link rel="stylesheet" href="style.css" />
</head>
<body>
  <div class="chat-container">
    <h1>Gemini Chatbot</h1>
    <textarea id="message" placeholder="Ask something..."></textarea>
    <button onclick="sendMessage()">Send</button>
    <div id="response"></div>
  </div>
  <script src="script.js"></script>
</body>
</html>
```
`style.css`
```CSS
body {
  font-family: Arial, sans-serif;
  background: #f4f4f4;
  display: flex;
  justify-content: center;
  padding-top: 50px;
}

.chat-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 0 10px rgba(0,0,0,0.2);
}

textarea {
  width: 100%;
  height: 80px;
  margin-bottom: 10px;
}

button {
  padding: 10px;
  width: 100%;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

#response {
  margin-top: 15px;
  white-space: pre-wrap;
}
```

`script.js`

```JavaScript 
async function sendMessage() {
  const msg = document.getElementById("message").value;
  const response = await fetch("https://your-ebs-url/api/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message: msg })
  });
  const reply = await response.text();
  document.getElementById("response").innerText = reply;
}
```



## 4. Run Locally
```Bash
./mvnw spring-boot:run
```
Then open `index.html` in your browser.



## 5. Deploy to AWS Elastic Beanstalk

### Step 1: Initialize Project

```bash
eb init -p java-17 gemini-chatbot
```

### Step 2: Create Environment
```bash
eb create gemini-chat-env
```

### Step 3: Deploy

```bash
eb deploy
```

Step 4: Set Gemini API Key in Environment Variables

### In AWS Console:

Go to your Elastic Beanstalk app > Configuration > Software

### Add Environment Variable:

```
GEMINI_API_KEY=your_api_key_here
```

---

## 6. Demo

Frontend: Upload the HTML/CSS/JS files to S3 (with static hosting) or integrate into a Spring Boot template.

https://springbootchatbotbyrushimithagare.us-east-1.elasticbeanstalk.com/



Backend:
 ``https://your-ebs-env.elasticbeanstalk.com/api/chat``


## 7. Known Issues

- **Mobile Browser Compatibility:**  
  The application might not work properly on some **mobile devices or mobile browsers** due to:
  - CORS or mixed content errors (HTTPS frontend calling HTTP backend)
  - Insecure HTTP calls blocked by mobile browsers
  - UI not being responsive yet for smaller screens
  - AWS backend URL not whitelisted properly on mobile networks

### Temporary Workaround:
- Test using desktop browser
- Use HTTPS for both frontend and backend
- Ensure CORS is enabled correctly on Spring Boot
- Use responsive CSS or add media queries for better mobile UX


### Author

Rushikesh Mithagare
Student @ Swami Ramanand Teerth Marathwada University, Nanded
Royal Education Society College of Computer Science and IT, Latur
Graduating April 2025
LinkedIn



