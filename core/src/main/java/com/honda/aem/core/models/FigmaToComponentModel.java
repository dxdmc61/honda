package com.honda.aem.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FigmaToComponentModel {

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String aiProvider;

    @ValueMapValue
    private String apiKey;

    @ValueMapValue
    private String prompt;

    @ValueMapValue
    private String projectDirectory;

    @ValueMapValue
    private String jenkinsUrl;

    public String getServletPath() {
        return "/bin/honda/generate-component";
    }

    // Getters for all properties
    public String getAiProvider() {
        return aiProvider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }
}
