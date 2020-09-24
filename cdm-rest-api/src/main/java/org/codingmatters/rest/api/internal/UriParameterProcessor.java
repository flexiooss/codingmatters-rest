package org.codingmatters.rest.api.internal;

import org.codingmatters.rest.api.RequestDelegate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nelt on 4/27/17.
 */
public class UriParameterProcessor {
    private final RequestDelegate requestDelegate;

    private final TreeMap<String, List<String>> parameters;
    private final LinkedList<String> names;
    private final StringBuilder regex;

    public UriParameterProcessor(RequestDelegate requestDelegate) {
        this.requestDelegate = requestDelegate;
        this.parameters = new TreeMap<>();
        this.names = new LinkedList<>();
        this.regex = new StringBuilder();
    }

    public Map<String, List<String>> process(String pathExpression) {
        this.parseTemplateExpression(pathExpression);
        this.gatherParameters();
        return this.parameters;
    }

    private void parseTemplateExpression(String pathExpression) {
        Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(pathExpression);
        int lastEnd = 0;
        while(matcher.find()) {
            int start = matcher.start();
            this.regex
                    .append(pathExpression.substring(lastEnd, start))
                    .append("([^/]*)")
            ;
            lastEnd = matcher.end();
            this.names.add(matcher.group(1));
            this.parameters.put(matcher.group(1), new LinkedList<>());
        }
        this.regex.append(pathExpression.substring(lastEnd, pathExpression.length()));
    }

    private void gatherParameters() {
        Matcher pathMatcher = this.requestDelegate.pathMatcher(this.regex.toString());
        if(pathMatcher.matches()) {
            for(int i = 1 ; i <= pathMatcher.groupCount() ; i++) {
                this.parameters.get(this.names.get(i - 1)).add(pathMatcher.group(i));
            }
        }
    }
}
