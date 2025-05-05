let typingInterval; // Interval ID for typing effect
let isTyping = false; // Flag to check if typing is in progress
let currentGeminiResponse = ""; // Store full Gemini response to complete on interrupt

// Append a new message to the chat box
function appendMessage(content, isUser = true) {
    const chatBox = document.getElementById("chat-box");
    const messageDiv = document.createElement("div");
    messageDiv.classList.add("message");
    messageDiv.classList.add(isUser ? "user-message" : "gemini-message");
    messageDiv.textContent = content;
    chatBox.appendChild(messageDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// Send the message when clicking the Send button
function sendMessage() {
    const message = document.getElementById("message").value;
    const responseDiv = document.getElementById("response");
    responseDiv.innerHTML = '';

    if (!message.trim()) {
        responseDiv.innerHTML = '<p class="error">Please enter a message.</p>';
        return;
    }

    appendMessage(message, true);
    document.getElementById("message").value = "";
    appendMessage('Sending message...', false);

    fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: message }),
    })
    .then(response => response.json())
    .then(data => {
        const chatBox = document.getElementById("chat-box");
        const lastMessage = chatBox.lastChild;
        if (lastMessage && lastMessage.textContent === "Sending message...") {
            lastMessage.remove();
        }

        const geminiResponse = data.response || "No valid response from Gemini.";
        currentGeminiResponse = geminiResponse; // Save the full response
        simulateTyping(geminiResponse);
    })
    .catch(error => {
        responseDiv.innerHTML = '<p class="error">There was an error processing your request.</p>';
        console.error(error);
    });
}

// Simulate typing effect by displaying one character at a time
function simulateTyping(text) {
    const chatBox = document.getElementById("chat-box");
    const geminiMessageDiv = document.createElement("div");
    geminiMessageDiv.classList.add("message", "gemini-message");
    chatBox.appendChild(geminiMessageDiv);
    chatBox.scrollTop = chatBox.scrollHeight;

    let index = 0;
    isTyping = true;
    typingInterval = setInterval(() => {
        if (index < text.length) {
            geminiMessageDiv.textContent += text.charAt(index);
            index++;
        } else {
            clearInterval(typingInterval);
            isTyping = false;
        }
    }, 20);
}

// Interrupt typing and display the full response
function interruptTyping() {
    if (isTyping) {
        clearInterval(typingInterval);
        isTyping = false;
        const chatBox = document.getElementById("chat-box");
        const lastMessage = chatBox.lastChild;
        lastMessage.textContent = currentGeminiResponse || "Interrupted.";
    }
}

// Press Enter to send
function checkEnter(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
}

// theme changer
const themeToggle = document.getElementById('theme-toggle');

themeToggle.addEventListener('click', () => {
    document.body.classList.toggle('dark-theme');
    themeToggle.textContent = document.body.classList.contains('dark-theme') ? '☀️' : '🌙';
});

// Clear button functionality
function clearChat() {
    // Clear the message input field
    document.getElementById('message').value = '';

    // Clear the chat history or any responses displayed
    const chatBox = document.getElementById('chat-box');
    chatBox.innerHTML = '';
    document.getElementById('response').innerHTML = '';

    // Stop any typing intervals if active
    if (isTyping) {
        clearInterval(typingInterval);
        isTyping = false;
    }

    // Hide the interrupt button
    const interruptButton = document.getElementById('interrupt-btn');
    interruptButton.style.display = "none";
}
