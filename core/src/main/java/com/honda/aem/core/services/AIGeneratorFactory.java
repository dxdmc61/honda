package com.honda.aem.core.services;

import com.honda.aem.core.services.imp.ChatGPTGenerator;
import com.honda.aem.core.services.imp.DeepSeekGenerator;

public class AIGeneratorFactory {
    public static AIGenerator getGenerator(String provider, String apiKey) {
        switch (provider.toLowerCase()) {
            case "deepseek":
                return new DeepSeekGenerator(apiKey);
            case "chatgpt":
                return new ChatGPTGenerator(apiKey);
            case "gemini":
                //return new GeminiGenerator(apiKey);
            default:
                throw new IllegalArgumentException("Unsupported AI provider: " + provider);
        }
    }
}