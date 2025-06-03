package com.honda.aem.core.services.imp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Part;

import com.honda.aem.core.services.AIGenerator;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepSeekGenerator implements AIGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(DeepSeekGenerator.class);
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-coder";
    
    private final String apiKey;
    
    public DeepSeekGenerator(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    public Map<String, String> generateComponent(Part figmaFile, String componentName, String prompt) throws Exception {
        Map<String, String> generatedFiles = new HashMap<>();
        
        // 1. Parse Figma file (assuming JSON export)
        String figmaJson = parseFigmaFile(figmaFile);
        
        // 2. Generate prompts for each AEM file type
        Map<String, String> prompts = createPrompts(componentName, figmaJson, prompt);
        
        // 3. Call DeepSeek API for each file type
        for (Map.Entry<String, String> entry : prompts.entrySet()) {
            String fileType = entry.getKey();
            String filePrompt = entry.getValue();
            
            LOG.debug("Generating {} for component {}", fileType, componentName);
            String generatedCode = callDeepSeekAPI(filePrompt);
            generatedFiles.put(fileType, generatedCode);
        }
        
        return generatedFiles;
    }
    
    private String parseFigmaFile(Part figmaFile) throws Exception {
        try (InputStream inputStream = figmaFile.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
    
    private Map<String, String> createPrompts(String componentName, String figmaJson, String customPrompt) {
        Map<String, String> prompts = new HashMap<>();
        
        String basePrompt = String.format(
            "Generate AEM component '%s' based on this Figma design:\n%s\n\n" +
            "Additional requirements:\n%s\n\n" +
            "Follow these AEM best practices:\n" +
            "- Use HTL (HTML Template Language) with data-sly attributes\n" +
            "- For dialogs, use granite/ui components\n" +
            "- For Java models, use Sling Models with OSGi annotations\n" +
            "- For clientlibs, use BEM CSS methodology\n" +
            "- Make all authorable fields configurable via dialog\n",
            componentName, figmaJson, customPrompt);
        
        prompts.put("html", basePrompt + "Generate the HTL markup file:");
        prompts.put("dialog", basePrompt + "Generate the _cq_dialog/.content.xml file:");
        prompts.put("editConfig", basePrompt + "Generate the _cq_editConfig.xml file:");
        prompts.put("css", basePrompt + "Generate the CSS file for the component's clientlib:");
        prompts.put("js", basePrompt + "Generate the JavaScript file for the component's clientlib:");
        prompts.put("model", basePrompt + "Generate the Sling Model Java class:");
        
        return prompts;
    }
    
    private String callDeepSeekAPI(String prompt) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        requestBody.put("messages", messages);
        
        HttpURLConnection connection = (HttpURLConnection) new URL(DEEPSEEK_API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        
        connection.getOutputStream().write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
        
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("DeepSeek API request failed with code: " + responseCode);
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String response = reader.lines().collect(Collectors.joining());
            JSONObject jsonResponse = new JSONObject(response);
            
            return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
        }
    }
}