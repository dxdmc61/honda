package com.honda.aem.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class)

public class LoginModelValidator {

    @Self
    SlingHttpServletRequest request;

    @Inject
    @Optional
    private String j_username;

    @Inject
    @Optional
    private String j_password;

    private String errorMessage = "";

    @PostConstruct
    protected void init() {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            loginFormValidation(j_username, j_password);

        }
    }

    private void loginFormValidation(String userName, String password) {

        if (j_password == "") {
            errorMessage += "password should not empty";
        }
        if (j_username == "") {
            errorMessage += "user name should not empty";
        }

    }

    public String getJ_username() {
        return j_username;
    }

    public String getJ_password() {
        return j_password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
