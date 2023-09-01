package org.codingmatters.rest.api.client.caching.key;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CacheKey;

public class QueryKey implements CacheKey {
    static public CacheKey query() {
        return new QueryKey();
    }
    @Override
    public String key(Request request) {
        return request.url().url().getQuery();
    }
}
