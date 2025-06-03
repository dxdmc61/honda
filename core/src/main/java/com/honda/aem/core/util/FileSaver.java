package com.honda.aem.core.util;


import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component(service = FileSaver.class)
public class FileSaver {
    private static final Logger LOG = LoggerFactory.getLogger(FileSaver.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private static final String[] COMPONENT_PATHS = {
            "/components/%s/%s.html", // HTL file
            "/components/%s/_cq_dialog/.content.xml", // Dialog XML
            "/components/%s/_cq_editConfig.xml", // Edit Config
            "/components/%s/%s.css", // Component CSS
            "/components/%s/%s.js", // Component JS
            "/components/%s/.content.xml" // Component definition
    };

    private static final String[] MODEL_PATHS = {
            "/core/src/main/java/com/honda/core/models/%sModel.java" // Sling Model
    };

    private static final String[] CLIENTLIB_PATHS = {
            "/clientlibs/%s/css.txt",
            "/clientlibs/%s/js.txt",
            "/clientlibs/%s/.content.xml"
    };

    public void saveFiles(String projectRoot, String componentName, Map<String, String> generatedFiles)
            throws IOException {
        // Validate inputs
        if (projectRoot == null || projectRoot.isEmpty()) {
            throw new IllegalArgumentException("Project directory cannot be null or empty");
        }

        if (componentName == null || componentName.isEmpty()) {
            throw new IllegalArgumentException("Component name cannot be null or empty");
        }

        // Create component directory structure
        createDirectoryStructure(projectRoot, componentName);

        // Save all generated files
        for (Map.Entry<String, String> entry : generatedFiles.entrySet()) {
            String fileType = entry.getKey();
            String content = entry.getValue();

            try {
                switch (fileType) {
                    case "html":
                        saveFile(projectRoot, String.format("/components/%s/%s.html", componentName, componentName),
                                content);
                        break;
                    case "dialog":
                        saveFile(projectRoot, String.format("/components/%s/_cq_dialog/.content.xml", componentName),
                                content);
                        break;
                    case "editConfig":
                        saveFile(projectRoot, String.format("/components/%s/_cq_editConfig.xml", componentName),
                                content);
                        break;
                    case "css":
                        saveFile(projectRoot, String.format("/components/%s/%s.css", componentName, componentName),
                                content);
                        break;
                    case "js":
                        saveFile(projectRoot, String.format("/components/%s/%s.js", componentName, componentName),
                                content);
                        break;
                    case "componentDefinition":
                        saveFile(projectRoot, String.format("/components/%s/.content.xml", componentName), content);
                        break;
                    case "model":
                        saveFile(projectRoot,
                                String.format("/core/src/main/java/com/honda/core/models/%sModel.java",
                                        capitalizeFirstLetter(componentName)),
                                content);
                        break;
                    case "clientlibCss":
                        saveFile(projectRoot, String.format("/clientlibs/%s/css.txt", componentName), content);
                        break;
                    case "clientlibJs":
                        saveFile(projectRoot, String.format("/clientlibs/%s/js.txt", componentName), content);
                        break;
                    case "clientlibDefinition":
                        saveFile(projectRoot, String.format("/clientlibs/%s/.content.xml", componentName), content);
                        break;
                    default:
                        LOG.warn("Unknown file type: {}", fileType);
                }
            } catch (IOException e) {
                LOG.error("Failed to save {} for component {}", fileType, componentName, e);
                throw e;
            }
        }

        LOG.info("Successfully saved all files for component: {}", componentName);
    }

    private void createDirectoryStructure(String projectRoot, String componentName) throws IOException {
        // Create component directory
        createDirectory(projectRoot + "/components/" + componentName);
        createDirectory(projectRoot + "/components/" + componentName + "/_cq_dialog");

        // Create clientlib directory if needed
        createDirectory(projectRoot + "/clientlibs/" + componentName);

        // Create model package if needed
        createDirectory(projectRoot + "/core/src/main/java/com/honda/core/models");
    }

    private void createDirectory(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + path);
            }
            LOG.debug("Created directory: {}", path);
        }
    }

    private void saveFile(String projectRoot, String relativePath, String content) throws IOException {
        Path fullPath = Paths.get(projectRoot, relativePath);
        Files.createDirectories(fullPath.getParent());
        Files.write(fullPath, content.getBytes(StandardCharsets.UTF_8));
        LOG.debug("Saved file: {}", fullPath);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Alternative method to save directly to JCR (AEM repository)
     */
    public void saveToRepository(ResourceResolver resolver, String componentPath, Map<String, String> generatedFiles)
            throws RepositoryException {
        // Implementation for saving directly to AEM repository would go here
        // Requires more complex JCR node creation logic
    }
}