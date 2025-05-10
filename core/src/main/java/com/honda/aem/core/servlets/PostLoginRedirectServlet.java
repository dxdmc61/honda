package com.honda.aem.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;

@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "=Post Login Redirect Servlet",
        "sling.servlet.paths=/bin/honda/post-login",
        "sling.servlet.methods=GET"
    }
)
public class PostLoginRedirectServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        ResourceResolver resolver = request.getResourceResolver();
        try {
            UserManager userManager = resolver.adaptTo(UserManager.class);
            if (userManager == null) {
                response.sendRedirect("/content/honda/us/en/login.html?error=user-mgr");
                return;
            }

            String userId = resolver.getUserID();
            Authorizable authorizable = userManager.getAuthorizable(userId);
            if (authorizable == null || authorizable.isGroup()) {
                response.sendRedirect("/content/honda/us/en/login.html?error=unauthorized");
                return;
            }

            Iterator<Group> groups = authorizable.memberOf();
            String redirectPath = "/content/honda/us/en.html"; // default fallback

            while (groups.hasNext()) {
                Group group = groups.next();
                String groupId = group.getID();

                switch (groupId) {
                    case "honda-admin":
                        redirectPath = "/content/honda/us/en/honda-admin.html";
                        break;
                    case "honda-auto":
                        redirectPath = "/content/honda/us/en/honda-auto.html";
                        break;
                    case "mc-pe":
                        redirectPath = "/content/honda/us/en/mc-pe.html";
                        break;
                }
            }

            response.sendRedirect(redirectPath);

        } catch (RepositoryException e) {
            response.sendRedirect("/content/honda/us/en/login.html?error=exception");
        }
    }
}

