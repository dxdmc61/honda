package com.honda.aem.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.honda.aem.core.service.WarrantyClaimsService;
import com.honda.aem.core.models.SearchOption;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Search Claims Component Sling Model for AEM 6.5 Cloud
 */
@Model(
        adaptables = {SlingHttpServletRequest.class, Resource.class},
        resourceType = "honda/components/content/searchclaims",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SearchClaimsModel {

    private static final Logger LOG = LoggerFactory.getLogger(SearchClaimsModel.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    @OSGiService
    private SlingSettingsService slingSettingsService;

    // @OSGiService
    // private WarrantyClaimsService warrantyClaimsService;

    @ValueMapValue
    @Default(values = "search-claims")
    private String id;

    @ValueMapValue
    @Default(values = "Search Claims")
    private String title;

    @ValueMapValue
    @Default(values = "/services/warranty/claims")
    private String apiEndpoint;

    @ValueMapValue
    @Default(values = "POST")
    private String apiMethod;

    @ValueMapValue
    @Default(values = "application/json")
    private String apiContentType;

    @ValueMapValue
    @Default(values = "30000")
    private int apiTimeout;

    @ValueMapValue
    @Default(values = "Search By")
    private String searchTypeLabel;

    @ValueMapValue
    @Default(values = "Search Value")
    private String searchValueLabel;

    @ValueMapValue
    @Default(values = "Date From")
    private String dateFromLabel;

    @ValueMapValue
    @Default(values = "Date To")
    private String dateToLabel;

    @ValueMapValue
    @Default(values = "Enter search value...")
    private String searchValuePlaceholder;

    @ValueMapValue
    @Default(values = "Clear Filters")
    private String clearButtonText;

    @ValueMapValue
    @Default(values = "outline")
    private String clearButtonType;

    @ValueMapValue
    @Default(values = "Search")
    private String searchButtonText;

    @ValueMapValue
    @Default(values = "primary")
    private String searchButtonType;

    @ValueMapValue
    @Default(values = "true")
    private boolean showSpinner;

    @ValueMapValue
    @Default(values = "Please select a search option")
    private String selectSearchTypeMessage;

    @ValueMapValue
    @Default(values = "Please enter a search value")
    private String enterSearchValueMessage;

    @ValueMapValue
    @Default(values = "From date cannot be after To date")
    private String dateRangeMessage;

    @ValueMapValue
    @Default(values = "Search successful! Results displayed below.")
    private String successMessage;

    @ValueMapValue
    @Default(values = "An error occurred while processing your search. Please try again.")
    private String errorMessage;

    @ValueMapValue
    @Default(values = "No records found matching your search criteria.")
    private String noResultsMessage;

    @ValueMapValue
    @Default(values = "true")
    private boolean showAlerts;

    @ValueMapValue
    @Default(values = "false")
    private boolean showDebugConsole;

    @ValueMapValue
    @Default(values = "info")
    private String logLevel;

    private List<SearchOption> searchOptions;
    private String searchOptionsJson;

    @PostConstruct
    protected void init() {
        // Set component ID if not provided
        if (StringUtils.isBlank(id)) {
            id = "search-claims-" + System.currentTimeMillis();
        }

        // Initialize search options from component configuration
        searchOptions = loadSearchOptions();

        // Convert search options to JSON for client-side use
        try {
            searchOptionsJson = OBJECT_MAPPER.writeValueAsString(searchOptions);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing search options to JSON", e);
            searchOptionsJson = "[]";
        }

        // Enable debug console in author mode unless explicitly disabled
        if (!showDebugConsole) {
            showDebugConsole = slingSettingsService.getRunModes().contains("author");
        }

        // Log component initialization at appropriate level
        if ("debug".equals(logLevel)) {
            LOG.debug("SearchClaimsModel initialized with ID: {}, API endpoint: {}", id, apiEndpoint);
        } else if ("info".equals(logLevel)) {
            LOG.info("SearchClaimsModel initialized: {}", id);
        }
    }

    /**
     * Load search options from component configuration
     * @return List of SearchOption objects
     */
    private List<SearchOption> loadSearchOptions() {
        List<SearchOption> options = new ArrayList<>();

        // Check if search options are configured
        Resource optionsResource = resource.getChild("searchOptions");
        if (optionsResource != null && optionsResource.hasChildren()) {
            // Load options from configuration
            Iterable<Resource> optionResources = optionsResource.getChildren();
            for (Resource option : optionResources) {
                ValueMap properties = option.getValueMap();
                String value = properties.get("value", String.class);
                String text = properties.get("text", String.class);
                boolean isDefault = properties.get("default", false);

                if (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(text)) {
                    options.add(new SearchOption(value, text, isDefault));
                }
            }
        } else {
            // Use default options if not configured
            options.add(new SearchOption("repairOrder", "Repair Order", false));
            options.add(new SearchOption("vin", "VIN", false));
            options.add(new SearchOption("customer", "Customer Name", false));
            options.add(new SearchOption("part", "Part Number", false));
            options.add(new SearchOption("dealer", "Dealer Code", false));
        }

        return options;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public String getApiContentType() {
        return apiContentType;
    }

    public int getApiTimeout() {
        return apiTimeout;
    }

    public String getSearchTypeLabel() {
        return searchTypeLabel;
    }

    public String getSearchValueLabel() {
        return searchValueLabel;
    }

    public String getDateFromLabel() {
        return dateFromLabel;
    }

    public String getDateToLabel() {
        return dateToLabel;
    }

    public String getSearchValuePlaceholder() {
        return searchValuePlaceholder;
    }

    public String getClearButtonText() {
        return clearButtonText;
    }

    public String getClearButtonType() {
        return clearButtonType;
    }

    public String getSearchButtonText() {
        return searchButtonText;
    }

    public String getSearchButtonType() {
        return searchButtonType;
    }

    public boolean isShowSpinner() {
        return showSpinner;
    }

    public String getSelectSearchTypeMessage() {
        return selectSearchTypeMessage;
    }

    public String getEnterSearchValueMessage() {
        return enterSearchValueMessage;
    }

    public String getDateRangeMessage() {
        return dateRangeMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNoResultsMessage() {
        return noResultsMessage;
    }

    public boolean isShowAlerts() {
        return showAlerts;
    }

    public boolean isShowDebugConsole() {
        return showDebugConsole;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public List<SearchOption> getSearchOptions() {
        return searchOptions;
    }

    public String getSearchOptionsJson() {
        return searchOptionsJson;
    }
}