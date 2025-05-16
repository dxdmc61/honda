package com.honda.aem.core.coreOverride;

import com.adobe.aemds.guide.themes.model.Component;

public interface Image extends Component {
    String getScr();
    String getAlt();
    boolean displayPopupTitle();
    
}
