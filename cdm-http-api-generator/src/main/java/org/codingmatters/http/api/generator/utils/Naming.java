package org.codingmatters.http.api.generator.utils;

import java.util.function.Function;

/**
 * Created by nelt on 5/6/17.
 */
public class Naming {
    public String type(String ... parts) {
        return this.name(this::upperCaseFirst, parts);
    }

    public String property(String ... parts) {
        return this.name(this::lowerCaseFirst, parts);
    }

    private String name(Function<String, String> firstPartTransformer,  String ... parts) {
        if(parts == null) return null;
        if(parts.length == 0) return "";

        StringBuilder result = new StringBuilder(firstPartTransformer.apply(parts[0]));
        for(int i = 1 ; i < parts.length ; i++) {
            result.append(this.upperCaseFirst(parts[i]));
        }
        return result.toString();
    }

    private String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private String lowerCaseFirst(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
