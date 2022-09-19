package org.codingmatters.rest.api.client.caching;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.okhttp.HttpClientWrapper;

import java.io.IOException;
import java.util.*;

public class CachingHttpClientWrapper implements HttpClientWrapper {
    private final HttpClientWrapper delegate;

    private final List<RuleSpec> rules = new LinkedList<>();
    private final Map<String, TimestampedResponse> cache = Collections.synchronizedMap(new HashMap<>());

    public CachingHttpClientWrapper(HttpClientWrapper delegate) {
        this.delegate = delegate;
    }

    public CachingHttpClientWrapper addCachingRule(CachingRule rule, CacheKey cacheKey) {
        this.rules.add(new RuleSpec(rule, cacheKey));
        return this;
    }

    @Override
    public Response execute(Request request) throws IOException {
        Optional<CacheKey> cacheable = this.isCacheable(request);
        if (cacheable.isEmpty()) {
            return this.delegate.execute(request);
        } else {
            String cacheKey = cacheable.get().key(request);
            if (this.cache.containsKey(cacheKey)) {
                return this.cache.get(cacheKey).response;
            }

            Response response = this.delegate.execute(request);
            if (cacheKey != null) {
                this.cache.put(cacheKey, new TimestampedResponse(System.currentTimeMillis(), response));
            }
            return response;
        }
    }

    public void cleanup(long ttl) {
        for (String key : this.cache.keySet()) {
            if(System.currentTimeMillis() - this.cache.get(key).timestamp > ttl) {
                this.cache.remove(key);
            }
        }
    }

    private Optional<CacheKey> isCacheable(Request request) {
        for (RuleSpec rule : this.rules) {
            if(rule.rule.matches(request)) {
                return Optional.of(rule.cacheKey);
            }
        }
        return Optional.empty();
    }

    static private class RuleSpec {
        public final CachingRule rule;
        public final CacheKey cacheKey;

        public RuleSpec(CachingRule rule, CacheKey cacheKey) {
            this.rule = rule;
            this.cacheKey = cacheKey;
        }
    }

    static private class TimestampedResponse {
        public final long timestamp;
        public final Response response;

        public TimestampedResponse(long timestamp, Response response) {
            this.timestamp = timestamp;
            this.response = response;
        }
    }

}
