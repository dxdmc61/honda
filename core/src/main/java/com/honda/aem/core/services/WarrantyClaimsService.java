package com.honda.aem.core.services;

import java.util.List;
import java.util.Map;

/**
 * Service interface for warranty claims operations
 */
public interface WarrantyClaimsService {

    /**
     * Search for warranty claims
     * @param searchParams Map of search parameters
     * @return Search results
     */
    Map<String, Object> searchClaims(Map<String, Object> searchParams);

    /**
     * Get available search options for the search form
     * @return List of search options
     */
    List<Map<String, Object>> getSearchOptions();

    /**
     * Get static labels for the search component
     * @return Map of label keys and values
     */
    Map<String, String> getStaticLabels();
}