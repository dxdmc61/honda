document.addEventListener("DOMContentLoaded", function () {
  // Chatbot Elements
  const chatbotContainer = document.getElementById("chatbot-container");
  const closeBtn = document.getElementById("close-btn");
  const sendBtn = document.getElementById("send-btn");
  const chatbotInput = document.getElementById("chatbot-input");
  const chatbotMessages = document.getElementById("chatbot-messages");
  const chatbotIcon = document.getElementById("chatbot-icon");


  // Tab Elements  
  const chatSection = document.getElementById("chat-section");


  // ======== CHATBOT FUNCTIONALITY ========

  // Toggle chatbot visibility when clicking the icon
  chatbotIcon.addEventListener("click", function () {
      chatbotContainer.classList.remove("hidden");
      chatbotIcon.style.display = "none"; // Hide chat icon
  });

  // Close chatbot when clicking the close button
  closeBtn.addEventListener("click", function () {
      chatbotContainer.classList.add("hidden");
      chatbotIcon.style.display = "flex"; // Show chat icon again
  });

  // Ensure chat section is always visible
  chatSection.classList.remove("hidden");

  // Send message functionality
  sendBtn.addEventListener("click", sendMessage);
  chatbotInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
          sendMessage();
      }
  });

  function sendMessage() {
      const userMessage = chatbotInput.value.trim();
      if (userMessage) {
          appendMessage("user", userMessage);
          chatbotInput.value = "";
          getBotResponse(userMessage);
      }
  }

  function appendMessage(sender, message) {
      const messageElement = document.createElement("div");
      messageElement.classList.add("message", sender);
      
      // Check if message already contains HTML (like search results)
      if (message.includes('<div class="chat-search-result">')) {
          // Message already contains formatted HTML, use as-is
          messageElement.innerHTML = message;
      } else {
          // Convert URLs to clickable links for plain text messages
          const urlRegex = /(https?:\/\/[^\s]+|\/content\/[^\s]*|\/[^\s]*\.html)/g;
          const messageWithLinks = message.replace(urlRegex, function(url) {
              // Handle relative URLs by making them absolute
              const fullUrl = url.startsWith('/') ? window.location.origin + url : url;
              return `<a href="${fullUrl}" target="_blank" class="chat-link">${url}</a>`;
          });
          messageElement.innerHTML = messageWithLinks;
      }
      
      chatbotMessages.appendChild(messageElement);
      chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
  }

  async function getBotResponse(userMessage) {
      // Show a loading message
      appendMessage("bot", "Thinking...");
      
      // Hardcoded keyword mappings for servlet calls
      const keywordMappings = {
          "warranty": "warranty",
          "provide me the parts order": "parts order",
          "parts": "parts",
          "service": "service",
          "recalls": "recalls",
          "maintenance": "maintenance",
          "provide search results for warranty": "warranty",
          "search for keyword warranty": "warranty",
          "list all transactions for this user": "transactions"
      };
      
      const searchTerm = keywordMappings[userMessage.toLowerCase()] || userMessage;
      
      // Always call servlet with either mapped term or original user message
      performSearchForChat(searchTerm);
  }
  
  // New function to handle search specifically for chat responses
  function performSearchForChat(query) {
      fetch(`/bin/globalSearch?q=${encodeURIComponent(query)}`)
          .then(res => res.json())
          .then(data => {
              // Remove "Thinking..." message
              chatbotMessages.lastChild.remove();
              
              if (!data.results || data.results.length === 0) {
                  appendMessage("bot", "No results found for your query.");
                  return;
              }

              // Create formatted search results for chat display (styled like original popout)
              let resultHtml = `I found ${data.results.length} result(s):<br><br>`;
              
              data.results.forEach(({ title, path, description }) => {
                  const url = path.endsWith(".html") ? path : `${path}.html`;
                  
                  resultHtml += `
                      <div class="chat-search-result">
                          <a href="${url}" target="_blank">${title}</a>
                          ${description ? `<div class="chat-search-result-desc">${description}</div>` : ''}
                      </div>
                  `;
              });
              
              appendMessage("bot", resultHtml);
          })
          .catch(err => {
              console.error("Search error:", err);
              chatbotMessages.lastChild.remove();
              appendMessage("bot", "Sorry, I encountered an error while searching. Please try again.");
          });
  }

});