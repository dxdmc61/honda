package com.honda.aem.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component(service = Servlet.class, property = {
        "sling.servlet.path=/bin/gSearch",
        "sling.servlet.method=GET"
})
public class Gsearch extends SlingAllMethodsServlet {

    @Reference
    QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException{

        ResourceResolver reslover = request.getResourceResolver();
        Session session = reslover.adaptTo(Session.class);
        String sreachTerm = request.getParameter("q");

        Map<String, String> prediCateMap = new HashMap<String, String>();

        prediCateMap.put("fulltext", sreachTerm);
        prediCateMap.put("p.limit", "10");
        prediCateMap.put("p.offset", "50");
        prediCateMap.put("path", "/content/honda");

        PredicateGroup predicateGroup = PredicateGroup.create(prediCateMap);

        Query query = queryBuilder.createQuery(predicateGroup, session);
        SearchResult searchResult = query.getResult();

        JsonArray jsonArray = new JsonArray();

        for (Hit hit : searchResult.getHits()) {

            try {
                Resource resource = hit.getResource();
                Resource childResource = resource.getChild("jcr:content");
                ValueMap valueMap = childResource.getValueMap();
                String pageTitle = valueMap.get("jcr:title", resource.getName());

                String description = valueMap.get("jcr:description","");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("pageTitle", pageTitle);
                jsonObject.addProperty("pagedescription", description);
                jsonObject.addProperty("path", resource.getPath());
                jsonArray.add(jsonObject);
            } catch (RepositoryException e) {
                e.printStackTrace();
            }

        }

        response.getWriter().write(jsonArray.toString());

    }

}
