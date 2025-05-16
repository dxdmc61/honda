package com.honda.aem.core.coreOverride.impl;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.adobe.cq.wcm.core.components.models.Image;

@Model(adaptables = Resource.class, adapters = Image.class, resourceType = MyImageImpl.RESOURCETYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MyImageImpl implements Image {
    public static final String RESOURCETYPE = "honda/components/image";

    @Inject
    Image delegetImage;

    @Inject
    String alt;

    @Override
    public String getAlt() {
        return delegetImage.getAlt();
    }

    @Override
    public String getSrc() {
        return delegetImage.getSrc();
    }

    @Override
    public boolean displayPopupTitle() {
        return delegetImage.displayPopupTitle();
    }

    public String getAltTextWithSrc() {
        return "Image" + (alt != null ? alt : "no alt text");
    }

}
