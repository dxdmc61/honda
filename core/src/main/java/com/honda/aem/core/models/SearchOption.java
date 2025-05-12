package com.honda.aem.core.models;

/**
 * Model representing a search option in the search dropdown
 */
public class SearchOption {
    private String value;
    private String text;
    private boolean isDefault;

    /**
     * Constructor
     * @param value The option value
     * @param text The option display text
     */
    public SearchOption(String value, String text) {
        this(value, text, false);
    }

    /**
     * Constructor with default flag
     * @param value The option value
     * @param text The option display text
     * @param isDefault Whether this option is the default selection
     */
    public SearchOption(String value, String text, boolean isDefault) {
        this.value = value;
        this.text = text;
        this.isDefault = isDefault;
    }

    /**
     * Get the option value
     * @return The option value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the option display text
     * @return The option display text
     */
    public String getText() {
        return text;
    }

    /**
     * Check if this option is the default selection
     * @return True if this option is the default, false otherwise
     */
    public boolean isDefault() {
        return isDefault;
    }
}