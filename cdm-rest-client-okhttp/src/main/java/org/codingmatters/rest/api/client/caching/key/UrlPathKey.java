package org.codingmatters.rest.api.client.caching.key;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CacheKey;

public class UrlPathKey implements CacheKey {
    static public CacheKey path() {
        return new UrlPathKey();
    }

    @Override
    public String key(Request request) {
        return request.url().url().getPath();
    }
}
