package com.honda.aem.core.util;

public class LinkUtil {
    public static String formatLink(String link) {
        String formattedLink = null;
        if (!link.startsWith("/content/dam")&& !link.startsWith("/content/experience-fragments") && !link.endsWith(".html")) {
            formattedLink = link + ".html";
        } else {
            formattedLink = link;
        }

        return formattedLink;
    }
}
