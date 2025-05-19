document.addEventListener("DOMContentLoaded", function () {
  // Chatbot Elements
  const chatbotContainer = document.getElementById("chatbot-container");
  const closeBtn = document.getElementById("close-btn");
  const sendBtn = document.getElementById("send-btn");
  const chatbotInput = document.getElementById("chatbot-input");
  const chatbotMessages = document.getElementById("chatbot-messages");
  const chatbotIcon = document.getElementById("chatbot-icon");
  
  // Search Elements
  const searchInput = document.getElementById("search-input");
  const searchBtn = document.getElementById("search-btn");
  const resultsContainer = document.getElementById("search-results");
  
  // Tab Elements
  const chatTab = document.getElementById("chat-tab");
  const searchTab = document.getElementById("search-tab");
  const chatSection = document.getElementById("chat-section");
  const searchSection = document.getElementById("search-section");
  
  // Initialize Debounce Variable
  let debounce;
  
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
  
  // Tab switching functionality
  chatTab.addEventListener("click", function() {
      chatTab.classList.add("active-tab");
      searchTab.classList.remove("active-tab");
      chatSection.classList.remove("hidden");
      searchSection.classList.add("hidden");
  });
  
  searchTab.addEventListener("click", function() {
      searchTab.classList.add("active-tab");
      chatTab.classList.remove("active-tab");
      searchSection.classList.remove("hidden");
      chatSection.classList.add("hidden");
  });
  
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
      messageElement.textContent = message;
      chatbotMessages.appendChild(messageElement);
      chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
  }
  
  async function getBotResponse(userMessage) {
      const apiKey = "your-api-key"; // Replace with your OpenAI API key
      const apiUrl = "https://api.openai.com/v1/chat/completions";
      
      // Show a loading message
      const loadingId = Date.now();
      appendMessage("bot", "Thinking...");
      
      try {
          const response = await fetch(apiUrl, {
              method: "POST",
              headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${apiKey}`,
              },
              body: JSON.stringify({
                  model: "gpt-3.5-turbo",
                  messages: [{ role: "user", content: userMessage }],
                  max_tokens: 150,
              }),
          });
          
          const data = await response.json();
          const botMessage = data.choices[0].message.content;
          
          // Replace the loading message with the actual response
          const loadingElement = chatbotMessages.lastChild;
          loadingElement.textContent = botMessage;
      } catch (error) {
          console.error("Error fetching bot response:", error);
          
          // Replace loading message with error message
          const loadingElement = chatbotMessages.lastChild;
          loadingElement.textContent = "Sorry, something went wrong. Please try again.";
      }
  }
  
  // ======== SEARCH FUNCTIONALITY ========
  
  function performSearch(query) {
      if (!query || query.length < 3) {
          resultsContainer.innerHTML = '';
          return;
      }
      
      resultsContainer.innerHTML = '<p>Searching...</p>';
      
      fetch(`/bin/globalSearch?q=${encodeURIComponent(query)}`)
          .then(res => res.json())
          .then(data => {
              resultsContainer.innerHTML = '';
              
              if (!data.results || data.results.length === 0) {
                  resultsContainer.innerHTML = '<p>No suggestions found.</p>';
                  return;
              }
              
              data.results.forEach(item => {
                  const div = document.createElement('div');
                  div.classList.add('search-suggestion');
                  div.innerHTML = `
                      <a href="${item.path}.html">
                          <strong>${item.title}</strong><br>
                      </a>
                      <small>${item.description || ''}</small>
                  `;
                  resultsContainer.appendChild(div);
              });
              
              // If in chat mode, also send search results to chatbot
              if (!chatSection.classList.contains('hidden')) {
                  const resultsForChat = data.results.slice(0, 3); // Limit to top 3 results
                  let message = "Here are some search results:\n";
                  
                  resultsForChat.forEach(item => {
                      message += `- ${item.title}: ${item.path}.html\n`;
                  });
                  
                  appendMessage("bot", message);
              }
          })
          .catch(err => {
              console.error('Search error:', err);
              resultsContainer.innerHTML = '<p>Error fetching results.</p>';
              
              // If in chat mode, also show error in chat
              if (!chatSection.classList.contains('hidden')) {
                  appendMessage("bot", "Sorry, I couldn't perform the search. Please try again.");
              }
          });
  }
  
  // Auto-suggestions for search input
  searchInput.addEventListener('input', () => {
      const query = searchInput.value.trim();
      
      if (debounce) clearTimeout(debounce);
      
      if (query.length < 3) {
          resultsContainer.innerHTML = '';
          return;
      }
      
      debounce = setTimeout(() => performSearch(query), 300);
  });
  
  // Clear suggestions when clicking outside
  document.addEventListener('click', (e) => {
      if (!resultsContainer.contains(e.target) && e.target !== searchInput) {
          resultsContainer.innerHTML = '';
      }
  });
  
  // Search button functionality
  searchBtn.addEventListener('click', () => {
      const query = searchInput.value.trim();
      if (query.length >= 3) {
          // For direct search page navigation
          // window.location.href = `/content/honda/us/en/search.html?q=${encodeURIComponent(query)}`;
          
          // For in-chatbot search
          performSearch(query);
          
          // Also add search query to chat if in chat mode
          if (!chatSection.classList.contains('hidden')) {
              appendMessage("user", `Search for: ${query}`);
          }
      }
  });
  
  // Integrate search into chatbot messages
  chatbotInput.addEventListener('input', function() {
      const input = chatbotInput.value.trim();
      // If input starts with '/' and contains 'search' command, switch to search tab
      if (input.startsWith('/search ')) {
          const searchQuery = input.replace('/search ', '').trim();
          if (searchQuery.length >= 3) {
              // Switch to search tab
              searchTab.click();
              // Fill search input with query
              searchInput.value = searchQuery;
              // Perform search
              performSearch(searchQuery);
              // Clear chatbot input
              chatbotInput.value = '';
          }
      }
  });
  
  // Run search if query param exists
  const urlParams = new URLSearchParams(window.location.search);
  const queryFromURL = urlParams.get('q');
  if (queryFromURL && queryFromURL.length >= 3) {
      // Switch to search tab
      searchTab.click();
      // Fill search input with query
      searchInput.value = queryFromURL;
      // Perform search
      performSearch(queryFromURL);
  }
});