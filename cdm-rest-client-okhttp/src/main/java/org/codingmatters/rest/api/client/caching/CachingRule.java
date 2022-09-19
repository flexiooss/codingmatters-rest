package org.codingmatters.rest.api.client.caching;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.key.CompoundKey;
import org.codingmatters.rest.api.client.caching.key.HeaderKey;
import org.codingmatters.rest.api.client.caching.key.UrlPathKey;

@FunctionalInterface
public interface CachingRule {
    boolean matches(Request request);
}
