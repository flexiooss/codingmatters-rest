package org.codingmatters.rest.api.client.caching.rules;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CachingRule;

public class MethodMatches implements CachingRule {
    static public MethodMatches GET = new MethodMatches("get");
    static public MethodMatches HEAD = new MethodMatches("head");
    static public MethodMatches POST = new MethodMatches("post");
    static public MethodMatches PUT = new MethodMatches("put");
    static public MethodMatches PATCH = new MethodMatches("patch");
    static public MethodMatches DELETE = new MethodMatches("delete");

    private final String method;

    private MethodMatches(String method) {
        this.method = method;
    }

    @Override
    public boolean matches(Request request) {
        return request.method().equalsIgnoreCase(this.method);
    }
}
