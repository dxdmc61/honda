package com.honda.aem.core.services;

import java.util.Map;

import javax.servlet.http.Part;

public interface AIGenerator {
    Map<String, String> generateComponent(Part figmaFile, String componentName, String prompt) throws Exception;
}
