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
import javax.jcr.Value;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Iterator;

@Component(service = Servlet.class, property = {
        Constants.SERVICE_DESCRIPTION + "=Post Login Redirect Servlet",
        "sling.servlet.paths=/bin/honda/post-login",
        "sling.servlet.methods=GET"
})
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
            String redirectPath = "/content/honda/us/en.html"; // default

            StringBuilder groupIds = new StringBuilder();

            while (groups.hasNext()) {
                Group group = groups.next();
                String groupId = group.getID();

                if (groupIds.length() > 0)
                    groupIds.append(",");
                groupIds.append(groupId);

                switch (groupId) {
                    case "honda-admin":
                        createCookie(response, "currentGroup", "honda-admin");
                        break;
                    case "honda-auto":
                        createCookie(response, "currentGroup", "honda-auto");
                        break;
                    case "honda-mc-pe":
                        createCookie(response, "currentGroup", "honda-mc-pe");
                        break;
                }
                redirectPath = "/content/honda/us/en/home.html";
            }

            // Create cookies
            createCookie(response, "hondaUserId", userId);
            createCookie(response, "hondaGroups", groupIds.toString());

            // Optional: Add profile info if available
            String firstName = getAuthorizableProperty(authorizable, "profile/givenName");
            String lastName = getAuthorizableProperty(authorizable, "profile/familyName");
            String email = getAuthorizableProperty(authorizable, "profile/email");

            if (firstName != null)
                createCookie(response, "hondaFirstName", firstName);
            if (lastName != null)
                createCookie(response, "hondaLastName", lastName);
            if (email != null)
                createCookie(response, "hondaEmail", email);

            response.sendRedirect(redirectPath);

        } catch (RepositoryException e) {
            response.sendRedirect("/content/honda/us/en/login.html?error=exception");
        }
    }

    // Helper: Safely get authorizable property
    private String getAuthorizableProperty(Authorizable authorizable, String prop) throws RepositoryException {
        if (authorizable.hasProperty(prop)) {
            Value[] values = authorizable.getProperty(prop);
            if (values.length > 0) {
                return values[0].getString();
            }
        }
        return null;
    }

    // Helper: Create cookie with basic config
    private void createCookie(SlingHttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour
        cookie.setHttpOnly(false); // Set to true if you *don't* want client-side JS to access it
        response.addCookie(cookie);
    }
}