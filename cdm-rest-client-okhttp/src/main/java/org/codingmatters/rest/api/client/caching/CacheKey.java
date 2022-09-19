package org.codingmatters.rest.api.client.caching;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.key.CompoundKey;
import org.codingmatters.rest.api.client.caching.key.HeaderKey;
import org.codingmatters.rest.api.client.caching.key.UrlPathKey;

@FunctionalInterface
public interface CacheKey {
    String key(Request request);

    CacheKey REQUEST_ID_AND_PATH = CompoundKey.with("-", HeaderKey.name("X-Request-ID"), UrlPathKey.path());
}
