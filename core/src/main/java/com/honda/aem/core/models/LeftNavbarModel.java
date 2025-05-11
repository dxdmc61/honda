package com.honda.aem.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(adaptables = Resource.class)
public class LeftNavbarModel {

    @Inject
    @ChildResource(name = "navLinks")
    private List<LinkItem> navLinks;

    public List<LinkItem> getNavLinks() {
        return navLinks;
    }

    @Model(adaptables = Resource.class)
    public static class LinkItem {
        @Inject @Optional
        private String linkName;

        @Inject @Optional
        private String linkUrl;

        public String getLinkName() { return linkName; }
        public String getLinkUrl() { return linkUrl; }
    }
}
