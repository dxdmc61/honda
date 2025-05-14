package com.honda.aem.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {
        "sling.servlet.paths=/bin/globalSearch",
        "sling.servlet.methods=GET"
})
public class GlobalSearchServlet extends SlingAllMethodsServlet {

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = request.getParameter("q");
        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        Map<String, String> params = new HashMap<>();
        params.put("path", "/content");
        params.put("type", "cq:Page");
        params.put("fulltext", searchTerm);
        params.put("p.limit", "10");

        Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
        SearchResult result = query.getResult();

        JsonArray jsonResults = new JsonArray();
        try {
            for (Hit hit : result.getHits()) {
                Resource res = hit.getResource();
                String title = res.getValueMap().get("jcr:title", res.getName());
                String path = res.getPath();

                JsonObject obj = new JsonObject();
                obj.addProperty("title", title);
                obj.addProperty("path", path);
                jsonResults.add(obj);
            }
        } catch (Exception e) {

        }
        JsonObject responseJson = new JsonObject();
        responseJson.add("results", jsonResults);

        response.setContentType("application/json");
        response.getWriter().write(responseJson.toString());
    }
}
