package com.honda.aem.core.services.imp;

import com.honda.aem.core.services.AIGenerator;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatGPTGenerator implements AIGenerator {

    private final String apiKey;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public ChatGPTGenerator(String apiKey) {
        // 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
        this.apiKey = "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";
    }

    @Override
    public Map<String, String> generateComponent(Part figmaFile, String componentName, String prompt) throws Exception {
        String figmaJson = extractFigmaJson(figmaFile);
        String userPrompt = buildUserPrompt(componentName, prompt, figmaJson);
        String responseContent = callOpenAI(userPrompt);
        return parseGeneratedCode(responseContent);
    }

    private String extractFigmaJson(Part figmaFile) throws Exception {
        try (InputStream inputStream = figmaFile.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private String buildUserPrompt(String componentName, String prompt, String figmaJson) {
        return String.format(
                "You are an expert AEM developer.\n\n" +
                        "Your task is to generate a production-ready Adobe Experience Manager (AEM) component based on the provided information.\n\n"
                        +
                        "Component Name: %s\n\n" +
                        "Design Specification (Figma JSON):\n%s\n\n" +
                        "Additional User Prompt:\n%s\n\n" +
                        "Follow these AEM best practices:\n" +
                        "- Use HTL (HTML Template Language) with data-sly attributes\n" +
                        "- Use granite/ui components for dialogs\n" +
                        "- Use Sling Models (OSGi R6 annotations)\n" +
                        "- Follow BEM methodology for CSS\n" +
                        "- Ensure all fields are configurable via the dialog\n\n" +
                        "Return your response as a **JSON object** where:\n" +
                        "- Keys are filenames (e.g. `component.html`, `ComponentModel.java`, `dialog.xml`, `clientlib.css`, `clientlib.js`)\n"
                        +
                        "- Values are the full code content\n\n" +
                        "Respond with only the JSON object. Do not include explanation or Markdown formatting.",
                componentName, figmaJson, prompt);
    }

    private String callOpenAI(String userPrompt) throws Exception {
        JSONObject requestJson = new JSONObject();
        requestJson.put("model", "gpt-3.5-turbo");
        requestJson.put("temperature", 0.3);

        // Messages array
        List messages = new ArrayList<JSONObject>();
        messages.add(new JSONObject().put("role", "system").put("content",
                "You generate AEM component source files from design specs."));
        messages.add(new JSONObject().put("role", "user").put("content", userPrompt));
        requestJson.put("messages", messages);

        // Prepare connection
        HttpURLConnection connection = (HttpURLConnection) new URL(OPENAI_API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestJson.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = connection.getResponseCode();
        InputStream responseStream = (status == 200)
                ? connection.getInputStream()
                : connection.getErrorStream();

        String response = IOUtils.toString(responseStream, StandardCharsets.UTF_8);
        connection.disconnect();

        if (status != 200) {
            throw new RuntimeException("OpenAI API error: " + status + " - " + response);
        }

        // Parse the raw text content from OpenAI
        JSONObject responseJson = new JSONObject(response);
        return responseJson
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }

    private Map<String, String> parseGeneratedCode(String responseText) {
        String cleaned = responseText;

        // Remove code block formatting if present
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        try {
            JSONObject json = new JSONObject(new JSONTokener(cleaned));
            Map<String, String> files = new HashMap<>();
            for (String key : json.keySet()) {
                files.put(key, json.getString(key));
            }
            return files;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse AI JSON output. Response:\n" + responseText + "\n\nError: " + e.getMessage());
        }
    }
}
