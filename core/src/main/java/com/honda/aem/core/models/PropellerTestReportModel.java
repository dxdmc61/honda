
package com.honda.aem.core.models;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for the Propeller Test Report component.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PropellerTestReportModel {
    
    @ValueMapValue
    @Default(values = "propeller-test-report")
    private String id;
    
    @ValueMapValue
    @Default(values = "Propeller Test Report")
    private String title;
    
    @ValueMapValue
    @Default(values = "View and search propeller test reports")
    private String subtitle;
    
    @ValueMapValue
    @Default(values = "/api/propeller-test-reports")
    private String apiPath;
    
    @ValueMapValue
    @Default(intValues = 30)
    private int refreshTime;
    
    @ValueMapValue
    private String[] hullMaterialsArray;
    
    @ValueMapValue
    private String[] horsepowersArray;
    
    @ValueMapValue
    private String[] boatTypesArray;
    
    private List<String> hullMaterials;
    private List<String> horsepowers;
    private List<String> boatTypes;
    
    /**
     * Initialize the model with default values if needed.
     */
    @PostConstruct
    protected void init() {
        // Set default values if not configured in dialog
        if (hullMaterialsArray == null || hullMaterialsArray.length == 0) {
            hullMaterials = Arrays.asList("ALUMINUM", "FIBERGLASS", "COMPOSITE");
        } else {
            hullMaterials = Arrays.asList(hullMaterialsArray);
        }
        
        if (horsepowersArray == null || horsepowersArray.length == 0) {
            horsepowers = Arrays.asList("90", "100A", "115A", "150", "200", "225");
        } else {
            horsepowers = Arrays.asList(horsepowersArray);
        }
        
        if (boatTypesArray == null || boatTypesArray.length == 0) {
            boatTypes = Arrays.asList("MONOHULL", "INFLATABLE", "PONTOON");
        } else {
            boatTypes = Arrays.asList(boatTypesArray);
        }
    }
    
    /**
     * @return the component ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return the component title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * @return the component subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }
    
    /**
     * @return the API path
     */
    public String getApiPath() {
        return apiPath;
    }
    
    /**
     * @return the refresh time in minutes
     */
    public int getRefreshTime() {
        return refreshTime;
    }
    
    /**
     * @return the list of hull materials
     */
    public List<String> getHullMaterials() {
        return hullMaterials;
    }
    
    /**
     * @return the list of horsepowers
     */
    public List<String> getHorsepowers() {
        return horsepowers;
    }
    
    /**
     * @return the list of boat types
     */
    public List<String> getBoatTypes() {
        return boatTypes;
    }
}

