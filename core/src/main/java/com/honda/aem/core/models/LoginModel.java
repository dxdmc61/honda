package com.honda.aem.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class LoginModel {
    
    @ValueMapValue
    @Default(values = "honda.login")
    private String clientLibCategories;
    
    @ValueMapValue
    @Default(values = "/content/dam/honda/logos/honda-logo.png")
    private String logoPath;
    
    @ValueMapValue
    @Default(values = "Welcome to Honda Dealer Portal")
    private String headingText;
    
    @ValueMapValue
    @Default(values = "/j_security_check")
    private String formAction;
    
    @ValueMapValue
    @Default(values = "/bin/honda/post-login")
    private String resourcePath;
    
    @ValueMapValue
    @Default(values = "Dealer ID")
    private String usernamePlaceholder;
    
    @ValueMapValue
    @Default(values = "Password")
    private String passwordPlaceholder;
    
    @ValueMapValue
    @Default(values = "Login")
    private String submitButtonText;
    
    @ValueMapValue
    @Default(values = "/content/dam/honda/images/login-side-image.jpg")
    private String sideImagePath;
    
    @ValueMapValue
    @Default(values = "Honda Dealer Portal")
    private String sideImageAltText;
    
    @ValueMapValue
    @Default(booleanValues = false)
    private boolean showFooter;
    
    @ValueMapValue
    @Default(values = "© 2025 Honda Motor Co., Ltd. All Rights Reserved.")
    private String footerText;
    
    /**
     * Initialize the model with default values.
     */
    @PostConstruct
    protected void init() {
        // Any additional initialization logic can be added here
    }
    
    /**
     * @return the client library categories
     */
    public String getClientLibCategories() {
        return clientLibCategories;
    }
    
    /**
     * @return the logo path
     */
    public String getLogoPath() {
        return logoPath;
    }
    
    /**
     * @return the heading text
     */
    public String getHeadingText() {
        return headingText;
    }
    
    /**
     * @return the form action URL
     */
    public String getFormAction() {
        return formAction;
    }
    
    /**
     * @return the resource path
     */
    public String getResourcePath() {
        return resourcePath;
    }
    
    /**
     * @return the username placeholder text
     */
    public String getUsernamePlaceholder() {
        return usernamePlaceholder;
    }
    
    /**
     * @return the password placeholder text
     */
    public String getPasswordPlaceholder() {
        return passwordPlaceholder;
    }
    
    /**
     * @return the submit button text
     */
    public String getSubmitButtonText() {
        return submitButtonText;
    }
    
    /**
     * @return the side image path
     */
    public String getSideImagePath() {
        return sideImagePath;
    }
    
    /**
     * @return the side image alt text
     */
    public String getSideImageAltText() {
        return sideImageAltText;
    }
    
    /**
     * @return whether to show the footer
     */
    public boolean isShowFooter() {
        return showFooter;
    }
    
    /**
     * @return the footer text
     */
    public String getFooterText() {
        return footerText;
    }
}

