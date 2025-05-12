package com.honda.aem.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import com.honda.aem.core.util.LinkUtil;


@Model(adaptables = Resource.class, resourceType = "honda/components/globalNavigation", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json")
public class GlobalNavigationModel {

    @ChildResource
    private List<GlobalNavigationItemModel> primaryMenu;

    @Inject
    private String linkURL;

    public String getLinkURL() {
        return LinkUtil.formatLink(linkURL);
    }

    public List<GlobalNavigationItemModel> getPrimaryMenu() {
        return primaryMenu;
    }

}
