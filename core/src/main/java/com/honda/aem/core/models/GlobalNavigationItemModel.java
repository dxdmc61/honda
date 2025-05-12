package com.honda.aem.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.honda.aem.core.util.LinkUtil;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GlobalNavigationItemModel {

    @Inject
    private String linkText;

    @Inject
    private String linkTarget;

    @Inject
    private String linkURL;

    public String getLinkURL() {
        return LinkUtil.formatLink(linkURL);
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public String getLinkText() {
        return linkText;
    }

}
