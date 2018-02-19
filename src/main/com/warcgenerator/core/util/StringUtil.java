package com.warcgenerator.core.util;

import java.util.Collection;

public class StringUtil {

    public static String collectionToString(Collection<String> collection, String separator) {
        StringBuilder builder = new StringBuilder();

        for (String item : collection) {
            builder.append(item);
            builder.append(separator);
        }
        if (builder.length() > 1) {
            // Remove last separator
            builder.deleteCharAt(builder.length()-1);
        }

        return builder.toString();
    }
}
