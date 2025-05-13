package com.honda.aem.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.honda.aem.core.models.SideNavigation;
import com.honda.aem.core.models.SideNavigation.NavigationItem;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = SideNavigation.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        resourceType = SideNavigationImpl.RESOURCE_TYPE
)
public class SideNavigationImpl implements SideNavigation {

    private static final Logger LOG = LoggerFactory.getLogger(SideNavigationImpl.class);

    // Update this to match your actual component path in the JCR repository
    protected static final String RESOURCE_TYPE = "honda/components/sidenavigation";

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    @Default(values = "")
    private String navigationRoot;

    @ValueMapValue
    @Default(values = "true")
    private boolean showCurrentPageBranch;

    @ValueMapValue
    @Default(values = "3")
    private int structureDepth;

    @ValueMapValue
    @Default(values = "Side Navigation")
    private String title;

    @ValueMapValue
    @Default(values = "false")
    private boolean excludeCurrentPage;

    @ValueMapValue
    @Default(values = "false")
    private boolean showTitle;

    @ValueMapValue
    @Default(values = "false")
    private boolean expandAll;

    @ValueMapValue
    @Default(values = "0")
    private String structureStart;

    @ValueMapValue
    private String id;

    @ValueMapValue
    private String cssClass;

    private List<NavigationItem> items = Collections.emptyList();
    private Page rootPage;

    @PostConstruct
    protected void init() {
        LOG.debug("SideNavigation init - Starting initialization");
        try {
            Resource resource = request.getResource();
            LOG.debug("SideNavigation init - Component path: {}", resource.getPath());

            // Determine root page for navigation
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            LOG.debug("SideNavigation init - Current page: {}", currentPage != null ? currentPage.getPath() : "null");
            LOG.debug("SideNavigation init - Navigation root setting: {}", navigationRoot);
            LOG.debug("SideNavigation init - Structure depth: {}", structureDepth);
            LOG.debug("SideNavigation init - Structure start: {}", structureStart);
            LOG.debug("SideNavigation init - Show current page branch: {}", showCurrentPageBranch);

            // Validate structure depth
            if (structureDepth <= 0) {
                LOG.warn("Invalid structure depth value: {}. Setting to default value of 3", structureDepth);
                structureDepth = 3;
            }

            // Parse structureStart as integer
            int startLevel = 0;
            try {
                startLevel = Integer.parseInt(structureStart);
                LOG.debug("Using structure start level: {}", startLevel);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid structureStart value: {}. Using default of 0", structureStart);
            }

            if (StringUtils.isNotBlank(navigationRoot)) {
                // Use specified path as root
                rootPage = pageManager.getPage(navigationRoot);
                LOG.debug("SideNavigation - Using specified root: {}", rootPage != null ? rootPage.getPath() : "not found");
            } else if (showCurrentPageBranch && currentPage != null) {
                // Use a parent of the current page as root
                rootPage = findNavigationRoot(currentPage);
                LOG.debug("SideNavigation - Found branch root: {}", rootPage != null ? rootPage.getPath() : "not found");
            } else if (currentPage != null) {
                // Default to current page as root
                rootPage = currentPage;
                LOG.debug("SideNavigation - Using current page as root: {}", rootPage != null ? rootPage.getPath() : "null");
            }

            // Build navigation items if root page is found
            if (rootPage != null) {
                LOG.debug("SideNavigation - Building items from root: {}", rootPage.getPath());

                // Direct verification of child pages
                verifyChildPages(rootPage);

                // If using structure start, we might need to find the starting page
                if (startLevel > 0) {
                    Page startPage = findStartPage(rootPage, startLevel);
                    if (startPage != null && !startPage.equals(rootPage)) {
                        LOG.debug("Using start page at level {}: {}", startLevel, startPage.getPath());
                        items = buildNavigationItems(startPage, startLevel, startLevel);
                    } else {
                        LOG.debug("Could not find start page at level {}. Using root page.", startLevel);
                        items = buildNavigationItems(rootPage, 1, startLevel);
                    }
                } else {
                    items = buildNavigationItems(rootPage, 1, 0);
                }

                LOG.debug("SideNavigation - Built {} navigation items", items.size());

                // Log all items for debugging
                logNavigationItems(items, "");
            } else {
                LOG.warn("No valid root page found for Side Navigation component");
            }

            // Generate ID if not set
            if (StringUtils.isBlank(id)) {
                id = "side-nav-" + System.currentTimeMillis();
            }

            // Verify current page hierarchy
            if (currentPage != null) {
                LOG.debug("Current page hierarchy path: {}", currentPage.getPath());
                Page parent = currentPage.getParent();
                while (parent != null) {
                    LOG.debug("  Parent page: {}", parent.getPath());
                    parent = parent.getParent();
                }
            }

        } catch (Exception e) {
            LOG.error("Error building side navigation", e);
        }
    }

    /**
     * Find the page at the specified level in the hierarchy
     */
    private Page findStartPage(Page rootPage, int targetLevel) {
        if (targetLevel <= 1) {
            return rootPage;
        }

        String rootPath = rootPage.getPath();
        int rootDepth = StringUtils.countMatches(rootPath, "/");

        // If the current page is at the target level or deeper, walk up the tree
        if (currentPage != null) {
            String currentPath = currentPage.getPath();
            int currentDepth = StringUtils.countMatches(currentPath, "/");
            int relativeDepth = currentDepth - rootDepth;

            if (relativeDepth >= targetLevel - 1) {
                // We need to go up from current page to find the target level
                Page page = currentPage;
                for (int i = 0; i < relativeDepth - (targetLevel - 1); i++) {
                    if (page != null) {
                        page = page.getParent();
                    }
                }
                return page;
            }
        }

        // Otherwise start from root and follow the first child at each level
        Page page = rootPage;
        for (int level = 1; level < targetLevel; level++) {
            if (page == null) {
                break;
            }

            Iterator<Page> children = page.listChildren(new PageFilter());
            if (children.hasNext()) {
                page = children.next();
            } else {
                LOG.debug("Could not find child page at level {}", level);
                return null;
            }
        }

        return page;
    }

    /**
     * Find suitable navigation root page based on the current page
     */
    private Page findNavigationRoot(Page page) {
        LOG.debug("Finding navigation root for page: {}", page.getPath());

        // If the current page has children, use it as the root
        if (hasChildPages(page)) {
            LOG.debug("Current page has children, using as root");
            return page;
        }

        // Otherwise, navigate up to find a parent with children
        Page parent = page.getParent();
        while (parent != null && !"/content".equals(parent.getPath())) {
            LOG.debug("Checking parent: {}", parent.getPath());
            if (hasChildPages(parent)) {
                LOG.debug("Found parent with children: {}", parent.getPath());
                return parent;
            }
            parent = parent.getParent();
        }

        // Fallback to current page if no suitable parent found
        LOG.debug("No parent with children found, using current page");
        return page;
    }

    /**
     * Check if a page has any visible child pages
     */
    private boolean hasChildPages(Page page) {
        // Use a less restrictive filter
        PageFilter filter = new PageFilter() {
            @Override
            public boolean includes(Page page) {
                // Include all pages except those explicitly hidden
                return !page.isHideInNav();
            }
        };

        Iterator<Page> children = page.listChildren(filter);
        boolean hasChildren = children != null && children.hasNext();
        LOG.debug("Page {} has children: {}", page.getPath(), hasChildren);
        return hasChildren;
    }

    /**
     * Verify child pages exist directly for debugging
     */
    private void verifyChildPages(Page page) {
        LOG.debug("Direct verification of children for page: {}", page.getPath());
        try {
            Iterator<Page> directChildren = page.listChildren();
            int count = 0;
            while (directChildren.hasNext()) {
                Page child = directChildren.next();
                count++;
                LOG.debug("  Child page: {}, title: {}, hideInNav: {}",
                        child.getPath(),
                        child.getTitle(),
                        child.isHideInNav());
            }
            LOG.debug("Total direct child pages: {}", count);
        } catch (Exception e) {
            LOG.error("Error verifying child pages", e);
        }
    }

    /**
     * Log navigation items recursively for debugging
     */
    private void logNavigationItems(List<NavigationItem> items, String indent) {
        for (NavigationItem item : items) {
            LOG.debug("{}Item: {}, active: {}, inPath: {}, hasChildren: {}",
                    indent,
                    item.getTitle(),
                    item.isActive(),
                    item.isInPath(),
                    item.isHasChildren());

            if (item.isHasChildren() && item.getChildren() != null) {
                logNavigationItems(item.getChildren(), indent + "  ");
            }
        }
    }

    /**
     * Build the navigation structure recursively
     */
    private List<NavigationItem> buildNavigationItems(Page page, int currentLevel, int startLevel) {
        LOG.debug("Building navigation items for page: {}, level: {}, startLevel: {}",
                page.getPath(), currentLevel, startLevel);
        List<NavigationItem> navItems = new ArrayList<>();

        // Skip processing if we've exceeded the depth limit
        if (currentLevel > structureDepth + startLevel) {
            LOG.debug("Skipping - exceeded structure depth: {} (adjusted for startLevel: {})",
                    structureDepth, structureDepth + startLevel);
            return navItems;
        }

        // Skip the current page if excluded
        if (excludeCurrentPage && page.equals(currentPage)) {
            LOG.debug("Skipping - current page is excluded");
            return navItems;
        }

        // Process child pages
        PageFilter filter = new PageFilter() {
            @Override
            public boolean includes(Page page) {
                // Include all pages except those explicitly hidden
                return !page.isHideInNav();
            }
        };

        Iterator<Page> children = page.listChildren(filter);
        int childCount = 0;

        if (currentLevel < startLevel) {
            // We're below the start level - recursively process only the first child to continue walking down
            if (children.hasNext()) {
                Page firstChild = children.next();
                LOG.debug("Below start level, following path via: {}", firstChild.getPath());
                navItems.addAll(buildNavigationItems(firstChild, currentLevel + 1, startLevel));
            }
        } else {
            // At or above start level - normal processing
            while (children.hasNext()) {
                Page childPage = children.next();
                childCount++;
                LOG.debug("Processing child page: {}, title: {}", childPage.getPath(), childPage.getTitle());

                // Create navigation item for this page
                NavigationItemImpl navItem = new NavigationItemImpl();
                navItem.setTitle(childPage.getTitle());
                navItem.setPath(childPage.getPath());
                navItem.setUrl(childPage.getPath() + ".html");
                navItem.setActive(childPage.equals(currentPage));

                // Check if this page is in the current path hierarchy
                boolean isInPath = isInActivePath(childPage);
                navItem.setInPath(isInPath);
                LOG.debug("  Item: {}, active: {}, inPath: {}", navItem.getTitle(), navItem.isActive(), navItem.isInPath());

                // Process child pages recursively
                List<NavigationItem> childItems = buildNavigationItems(childPage, currentLevel + 1, startLevel);
                navItem.setChildren(childItems);
                navItem.setHasChildren(!childItems.isEmpty());

                // Set expanded state
                navItem.setExpanded(expandAll || isInPath);

                navItems.add(navItem);
            }
            LOG.debug("Added {} navigation items for page: {}", childCount, page.getPath());
        }

        return navItems;
    }

    /**
     * Check if a page is in the current path hierarchy
     */
    private boolean isInActivePath(Page page) {
        if (currentPage == null) {
            return false;
        }

        String pagePath = page.getPath();
        String currentPath = currentPage.getPath();

        boolean isInPath = currentPath.equals(pagePath) || currentPath.startsWith(pagePath + "/");
        LOG.debug("Checking if page is in path - Page: {}, CurrentPage: {}, Result: {}",
                pagePath, currentPath, isInPath);
        return isInPath;
    }

    @Override
    public List<NavigationItem> getItems() {
        return items;
    }

    @Override
    public Page getRootPage() {
        return rootPage;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isShowTitle() {
        return showTitle;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String getStructureStart() {
        return structureStart;
    }

    /**
     * Implementation of the NavigationItem interface.
     */
    public static class NavigationItemImpl implements SideNavigation.NavigationItem {
        private String title;
        private String path;
        private String url;
        private boolean active;
        private boolean inPath;
        private boolean hasChildren;
        private boolean expanded;
        private List<NavigationItem> children = new ArrayList<>();

        @Override
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public boolean isInPath() {
            return inPath;
        }

        public void setInPath(boolean inPath) {
            this.inPath = inPath;
        }

        @Override
        public boolean isHasChildren() {
            return hasChildren;
        }

        public void setHasChildren(boolean hasChildren) {
            this.hasChildren = hasChildren;
        }

        @Override
        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override
        public List<NavigationItem> getChildren() {
            return children;
        }

        public void setChildren(List<NavigationItem> children) {
            this.children = children;
        }
    }
}