package org.codingmatters.http.api.internal;

import org.codingmatters.http.api.RequestDeleguate;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 4/27/17.
 */
public class PathParameterProcessor {
    private final RequestDeleguate requestDeleguate;

    public PathParameterProcessor(RequestDeleguate requestDeleguate) {
        this.requestDeleguate = requestDeleguate;
    }

    public Map<String, String> process(String pathExpression) {
        Map<String, String> result = new TreeMap<>();
        LinkedList<String> names = new LinkedList<>();

        StringBuilder regex = new StringBuilder();

        Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(pathExpression);
        int lastEnd = 0;
        while(matcher.find()) {
            int start = matcher.start();
            regex
                    .append(pathExpression.substring(lastEnd, start))
                    .append("([^/]*)")
            ;
            lastEnd = matcher.end();
            names.add(matcher.group(1));
            result.put(matcher.group(1), null);
        }
        regex.append(pathExpression.substring(lastEnd, pathExpression.length()));

        Matcher pathMatcher = this.requestDeleguate.pathMatcher(regex.toString());
        if(pathMatcher.matches()) {
            for(int i = 1 ; i <= pathMatcher.groupCount() ; i++) {
                result.put(names.get(i - 1), pathMatcher.group(i));
            }
        }
        return result;
    }
}