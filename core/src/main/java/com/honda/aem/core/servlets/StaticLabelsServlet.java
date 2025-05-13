package com.honda.aem.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honda.aem.core.services.WarrantyClaimsService;

/**
 * Servlet that provides static labels for the Search Claims component
 */
@Component(service = Servlet.class, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/services/honda/searchclaims/labels",
        "sling.servlet.extensions=" + "json"
})
public class StaticLabelsServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(StaticLabelsServlet.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Reference
    private WarrantyClaimsService warrantyClaimsService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get labels from service
            Map<String, String> labels = warrantyClaimsService.getStaticLabels();

            // Set response headers
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Write labels to response
            OBJECT_MAPPER.writeValue(response.getWriter(), labels);

        } catch (Exception e) {
            LOG.error("Error retrieving static labels", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{ \"error\": \"Error retrieving static labels\" }");
        }
    }
}