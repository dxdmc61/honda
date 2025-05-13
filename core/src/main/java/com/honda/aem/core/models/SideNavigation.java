package com.honda.aem.core.models;

import java.util.List;

import com.day.cq.wcm.api.Page;

/**
 * Interface for the Side Navigation component.
 * This component creates a hierarchical navigation structure based on page hierarchy.
 */
public interface SideNavigation {

    /**
     * Returns the list of top-level navigation items.
     */
    List<NavigationItem> getItems();

    /**
     * Returns the root page used for navigation.
     */
    Page getRootPage();

    /**
     * Returns the title of the navigation component.
     */
    String getTitle();

    /**
     * Indicates whether the title should be displayed.
     */
    boolean isShowTitle();

    /**
     * Returns the HTML ID attribute for the component.
     */
    String getId();

    /**
     * Returns additional CSS classes for the component.
     */
    String getCssClass();

    /**
     * Returns the level at which to start the navigation structure.
     */
    String getStructureStart();

    /**
     * Navigation item interface. Represents a single item in the navigation hierarchy.
     */
    public interface NavigationItem {
        String getTitle();
        String getPath();
        String getUrl();
        boolean isActive();
        boolean isInPath();
        boolean isHasChildren();
        boolean isExpanded();
        List<NavigationItem> getChildren();
    }
}