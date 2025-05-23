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

import javax.jcr.Node;
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
        params.put("path", "/content/honda"); // Limit search to Honda content
        params.put("type", "cq:Page");
        params.put("fulltext", searchTerm);
        params.put("p.limit", "50");

        Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
        SearchResult result = query.getResult();

        JsonArray jsonResults = new JsonArray();
        int counter = 0;
        try {
            for (Hit hit : result.getHits()) {
                Resource pageRes = hit.getResource();
                Resource contentRes = pageRes.getChild("jcr:content");

                if (contentRes != null) {
                    ValueMap vm = contentRes.getValueMap();
                    String title = vm.get("jcr:title", pageRes.getName());
                    Node currentNode = contentRes.adaptTo(Node.class);
                    String nodeSearchCount = currentNode.getProperty("nodeSearchCount").toString();
                    if (nodeSearchCount == null) {
                        currentNode.setProperty("nodeSearchCount", counter++);
                    } else {
                        currentNode.setProperty("nodeSearchCount", Integer.parseInt(nodeSearchCount) + 1);
                    }
                    String description = vm.get("jcr:description", "");

                    JsonObject obj = new JsonObject();
                    obj.addProperty("title", title);
                    obj.addProperty("description", description);
                    obj.addProperty("path", pageRes.getPath());

                    jsonResults.add(obj);
                }
            }
            session.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject responseJson = new JsonObject();
        responseJson.add("results", jsonResults);

        response.setContentType("application/json");
        response.getWriter().write(responseJson.toString());
    }
}
