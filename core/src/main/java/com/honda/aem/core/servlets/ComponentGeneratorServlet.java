package com.honda.aem.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.honda.aem.core.services.AIGenerator;
import com.honda.aem.core.services.AIGeneratorFactory;
import com.honda.aem.core.util.FileSaver;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.sling.servlets.annotations.SlingServletPaths;
import java.io.IOException;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/honda/generate-component")
public class ComponentGeneratorServlet extends SlingAllMethodsServlet {

    @Reference
    private FileSaver fileSaver;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            String aiProvider = request.getParameter("aiProvider");
            String apiKey = request.getParameter("apiKey");
            String prompt = request.getParameter("prompt");
            String componentName = request.getParameter("componentName");
            String projectDirectory = request.getParameter("projectDirectory");
            String jenkinsUrl = request.getParameter("jenkinsUrl");

            // Process Figma file
            Part figmaFile = request.getPart("figmaFile");

            // Generate component using selected AI provider
            AIGenerator generator = AIGeneratorFactory.getGenerator(aiProvider, apiKey);
            Map<String, String> generatedFiles = generator.generateComponent(
                    figmaFile,
                    componentName,
                    prompt);

            // Save files to project directory
            fileSaver.saveFiles(projectDirectory, componentName, generatedFiles);

            // Trigger Jenkins build if URL provided
            if (jenkinsUrl != null && !jenkinsUrl.isEmpty()) {
                // JenkinsTrigger.triggerBuild(jenkinsUrl);
            }

            response.getWriter().write("Component generated successfully!");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}