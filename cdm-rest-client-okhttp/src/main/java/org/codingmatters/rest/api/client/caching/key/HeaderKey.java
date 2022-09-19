package org.codingmatters.rest.api.client.caching.key;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CacheKey;

public class HeaderKey implements CacheKey {

    static public CacheKey name(String name) {
        return new HeaderKey(name);
    }

    private final String name;

    public HeaderKey(String name) {
        this.name = name;
    }

    @Override
    public String key(Request request) {
        return request.header(this.name);
    }
}
