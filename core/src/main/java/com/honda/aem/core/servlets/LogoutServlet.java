package com.honda.aem.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "=Honda Logout Servlet",
        "sling.servlet.paths=/bin/honda/logout",
        "sling.servlet.methods=GET"
    }
)
public class LogoutServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        // Invalidate session
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        // Remove "hondaUserId" and other cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("hondaUserId".equals(cookie.getName()) || true) { // remove all cookies
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        // Redirect to login
        response.sendRedirect("/content/honda/us/en/login.html");
    }
}

