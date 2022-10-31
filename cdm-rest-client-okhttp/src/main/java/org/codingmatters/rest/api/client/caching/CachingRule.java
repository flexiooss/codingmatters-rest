package org.codingmatters.rest.api.client.caching;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.caching.key.CompoundKey;
import org.codingmatters.rest.api.client.caching.key.HeaderKey;
import org.codingmatters.rest.api.client.caching.key.UrlPathKey;

public interface CachingRule {
    default boolean matches(Request request) {
        return true;
    }
    default boolean matches(Response response) {
        return true;
    }

    CachingRule ACCEPT = new CachingRule() {
    };
    CachingRule DENY = new CachingRule() {
        @Override
        public boolean matches(Request request) {
            return false;
        }

        @Override
        public boolean matches(Response response) {
            return false;
        }
    };
}
