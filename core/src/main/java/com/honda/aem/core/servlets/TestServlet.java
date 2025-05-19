package com.honda.aem.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;

@Component(service = Servlet.class, property = {
        "sling.servlet.path=bin/postLogin",
        "sling.servlet.method=GET"
}

)
public class TestServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        try {
            ResourceResolver resolver = request.getResourceResolver();
            UserManager userManager = resolver.adaptTo(UserManager.class);

            if (userManager == null) {
                response.sendRedirect("/content/honda/us/en/login.html?error=unauth");
                return;

            }

            String userId = resolver.getUserID();
            Authorizable auth = userManager.getAuthorizable(userId);
            if (auth == null) {
                response.sendRedirect("/content/honda/us/en/login.html");
                return;

            }
            Iterator<Group> groups = auth.memberOf();
            while (groups.hasNext()) {
                Group group = groups.next();
                String groupId = group.getID();
                switch (groupId) {
                    case "honda-admin":
                        response.sendRedirect(resolver.map(request, "/content/honda/us/en/honda-admin.html"));
                        break;
                    case "honda-auto":
                        response.sendRedirect(resolver.map(request, "/content/honda/us/en/honda-auto"));
                        break;
                    case "honda-bike":
                        response.sendRedirect(resolver.map(request, "/content/honda/us/en/hond-bike"));
                        break;
                    case "honda-marine":
                         response.sendRedirect("/content/honda/en/honda-marine");
                    default:
                        break;
                }

            }

        } catch (Exception e) {

        }

    }

}
