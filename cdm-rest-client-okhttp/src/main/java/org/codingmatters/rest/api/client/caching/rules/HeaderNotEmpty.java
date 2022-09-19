package org.codingmatters.rest.api.client.caching.rules;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CachingRule;

public class HeaderNotEmpty implements CachingRule {
    static public CachingRule name(String headerName) {
        return new HeaderNotEmpty(headerName);
    }
    private final String headerName;

    public HeaderNotEmpty(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public boolean matches(Request request) {
        return request.header(this.headerName) != null && ! request.header(this.headerName).isEmpty();
    }
}
