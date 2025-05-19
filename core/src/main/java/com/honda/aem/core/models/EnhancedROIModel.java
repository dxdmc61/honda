package com.honda.aem.core.models;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sling Model for the Enhanced ROI Interface component.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class EnhancedROIModel {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @ValueMapValue
    @Default(values = "enhanced-roi")
    private String id;
    
    @ValueMapValue
    // @Default(values = "View and manage enhanced ROI reports")
    private String subtitle;
    
    @ValueMapValue
    @Default(values = "/api/enhancedroi")
    private String apiEndpoint;
    
    @ValueMapValue
    @Default(values = "Search Reports")
    private String searchBoxTitle;
    
    @ValueMapValue
    @Default(values = "Search By")
    private String searchByLabel;
    
    @ValueMapValue
    @Default(values = "Search Value")
    private String searchValueLabel;
    
    @ValueMapValue
    @Default(values = "Enter search value...")
    private String searchValuePlaceholder;
    
    @ValueMapValue
    @Default(values = "Date From")
    private String dateFromLabel;
    
    @ValueMapValue
    @Default(values = "Date To")
    private String dateToLabel;
    
    @ValueMapValue
    @Default(values = "dd/mm/yyyy")
    private String dateFormat;
    
    @ValueMapValue
    @Default(values = "Search")
    private String searchButtonText;
    
    @ValueMapValue
    @Default(values = "primary")
    private String searchButtonType;
    
    @ValueMapValue
    @Default(values = "Clear")
    private String clearButtonText;
    
    @ValueMapValue
    @Default(values = "outline")
    private String clearButtonType;
    
    @ValueMapValue
    @Default(booleanValues = true)
    private boolean showSpinner;
    
    @ValueMapValue
    @Default(values = "PRINTING")
    private String printingColumnLabel;
    
    @ValueMapValue
    @Default(values = "YES/NO")
    private String yesNoColumnLabel;
    
    @ValueMapValue
    @Default(values = "BATCH NUMBER")
    private String batchNumberColumnLabel;
    
    @ValueMapValue
    @Default(values = "DEALER INFO")
    private String dealerInfoColumnLabel;
    
    @ValueMapValue
    @Default(values = "ORDER TIME")
    private String orderTimeColumnLabel;
    
    @ValueMapValue
    @Default(values = "ITEM")
    private String itemColumnLabel;
    
    @ValueMapValue
    @Default(values = "LEVEL")
    private String levelColumnLabel;
    
    @ValueMapValue
    @Default(values = "PRICE")
    private String priceColumnLabel;
    
    @ValueMapValue
    @Default(values = "MORE ITEMS")
    private String itemCodeColumnLabel;
    
    @ValueMapValue
    @Default(values = "ITEM NAME")
    private String itemNameColumnLabel;
    
    @ValueMapValue
    @Default(values = "COLOR CODE")
    private String colorCodeColumnLabel;
    
    @ValueMapValue
    @Default(values = "COLOR NAME")
    private String colorNameColumnLabel;
    
    @ValueMapValue
    @Default(values = "Search completed successfully.")
    private String successMessage;
    
    @ValueMapValue
    @Default(values = "An error occurred while searching. Please try again.")
    private String errorMessage;
    
    @ValueMapValue
    @Default(values = "No results found matching your search criteria.")
    private String noResultsMessage;
    
    @ValueMapValue
    @Default(booleanValues = false)
    private boolean showDebugConsole;
    
    @ValueMapValue
    private String[] searchOptionsArray;
    
    private String searchOptionsJson;
   
    @PostConstruct
    protected void init() {
        if (searchOptionsArray == null || searchOptionsArray.length == 0) {
            searchOptionsArray = new String[] {
                "{\"value\":\"batchNumber\",\"text\":\"Batch Number\"}",
                "{\"value\":\"dealerInfo\",\"text\":\"Dealer Info\"}",
                "{\"value\":\"itemCode\",\"text\":\"Item Code\"}",
                "{\"value\":\"itemName\",\"text\":\"Item Name\"}"
            };
        }
        
        try {
            searchOptionsJson = convertSearchOptionsToJson();
        } catch (JsonProcessingException e) {
            searchOptionsJson = "[]";
        }
    }
    
    /**
     * Convert search options array to JSON string
     * @return JSON string representation of search options
     * @throws JsonProcessingException if JSON processing fails
     */
    private String convertSearchOptionsToJson() throws JsonProcessingException {
        if (searchOptionsArray == null || searchOptionsArray.length == 0) {
            return "[]";
        }
        
        return OBJECT_MAPPER.writeValueAsString(searchOptionsArray);
    }
    
    /**
     * @return the component ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return the subtitle text
     */
    public String getSubtitle() {
        return subtitle;
    }
    
    /**
     * @return the API endpoint URL
     */
    public String getApiEndpoint() {
        return apiEndpoint;
    }
    
    /**
     * @return the search box title
     */
    public String getSearchBoxTitle() {
        return searchBoxTitle;
    }
    
    /**
     * @return the search by label
     */
    public String getSearchByLabel() {
        return searchByLabel;
    }
    
    /**
     * @return the search value label
     */
    public String getSearchValueLabel() {
        return searchValueLabel;
    }
    
    /**
     * @return the search value placeholder
     */
    public String getSearchValuePlaceholder() {
        return searchValuePlaceholder;
    }
    
    /**
     * @return the date from label
     */
    public String getDateFromLabel() {
        return dateFromLabel;
    }
    
    /**
     * @return the date to label
     */
    public String getDateToLabel() {
        return dateToLabel;
    }
    
    /**
     * @return the date format
     */
    public String getDateFormat() {
        return dateFormat;
    }
    
    /**
     * @return the search button text
     */
    public String getSearchButtonText() {
        return searchButtonText;
    }
    
    /**
     * @return the search button type
     */
    public String getSearchButtonType() {
        return searchButtonType;
    }
    
    /**
     * @return the clear button text
     */
    public String getClearButtonText() {
        return clearButtonText;
    }
    
    /**
     * @return the clear button type
     */
    public String getClearButtonType() {
        return clearButtonType;
    }
    
    /**
     * @return whether to show the spinner
     */
    public boolean isShowSpinner() {
        return showSpinner;
    }
    
    /**
     * @return the printing column label
     */
    public String getPrintingColumnLabel() {
        return printingColumnLabel;
    }
    
    /**
     * @return the yes/no column label
     */
    public String getYesNoColumnLabel() {
        return yesNoColumnLabel;
    }
    
    /**
     * @return the batch number column label
     */
    public String getBatchNumberColumnLabel() {
        return batchNumberColumnLabel;
    }
    
    /**
     * @return the dealer info column label
     */
    public String getDealerInfoColumnLabel() {
        return dealerInfoColumnLabel;
    }
    
    /**
     * @return the order time column label
     */
    public String getOrderTimeColumnLabel() {
        return orderTimeColumnLabel;
    }
    
    /**
     * @return the item column label
     */
    public String getItemColumnLabel() {
        return itemColumnLabel;
    }
    
    /**
     * @return the level column label
     */
    public String getLevelColumnLabel() {
        return levelColumnLabel;
    }
    
    /**
     * @return the price column label
     */
    public String getPriceColumnLabel() {
        return priceColumnLabel;
    }
    
    /**
     * @return the item code column label
     */
    public String getItemCodeColumnLabel() {
        return itemCodeColumnLabel;
    }
    
    /**
     * @return the item name column label
     */
    public String getItemNameColumnLabel() {
        return itemNameColumnLabel;
    }
    
    /**
     * @return the color code column label
     */
    public String getColorCodeColumnLabel() {
        return colorCodeColumnLabel;
    }
    
    /**
     * @return the color name column label
     */
    public String getColorNameColumnLabel() {
        return colorNameColumnLabel;
    }
    
    /**
     * @return the success message
     */
    public String getSuccessMessage() {
        return successMessage;
    }
    
    /**
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * @return the no results message
     */
    public String getNoResultsMessage() {
        return noResultsMessage;
    }
    
    /**
     * @return whether to show the debug console
     */
    public boolean isShowDebugConsole() {
        return showDebugConsole;
    }
    
    /**
     * @return the search options as a JSON string
     */
    public String getSearchOptionsJson() {
        return searchOptionsJson;
    }
    
    /**
     * @return the search options
     */
    public List<String> getSearchOptions() {
        return Arrays.asList(searchOptionsArray);
    }
}