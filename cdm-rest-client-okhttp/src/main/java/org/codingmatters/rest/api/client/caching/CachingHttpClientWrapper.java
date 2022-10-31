package org.codingmatters.rest.api.client.caching;

import com.sun.source.doctree.SeeTree;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.caching.utils.ResponseReviver;
import org.codingmatters.rest.api.client.okhttp.HttpClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CachingHttpClientWrapper implements HttpClientWrapper {
    static private final Logger log = LoggerFactory.getLogger(CachingHttpClientWrapper.class);

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
        Optional<RuleSpec> rule = this.rule(request);
        if (rule.isEmpty()) {
            log.debug("[CACHE] not cached : {}", request.url().url());
            return this.delegate.execute(request);
        } else {
            String cacheKey = rule.get().cacheKey.key(request);
            if(cacheKey == null) return this.delegate.execute(request);

            TimestampedResponse cached = this.cache.get(cacheKey);
            if (cached != null) {
                log.debug("[CACHE] already cached [{}] : {}", cacheKey, request.url().url());
                return cached.reviver.revived();
            }

            Response response = this.delegate.execute(request);
            if(rule.get().rule.matches(response)) {
                ResponseReviver reviver = new ResponseReviver(response);
                this.cache.put(cacheKey, new TimestampedResponse(System.currentTimeMillis(), reviver));
                log.debug("[CACHE] added to cache [{}] : {}", cacheKey, request.url().url());
                return reviver.revived();
            } else {
                return response;
            }
        }
    }

    public void cleanup(long ttl) {
        LinkedList<String> toBeRemoved = new LinkedList<>();
        for (String key : this.cache.keySet()) {
            if(System.currentTimeMillis() - this.cache.get(key).timestamp > ttl) {
                toBeRemoved.add(key);
            }
        }
        for (String key : toBeRemoved) {
            this.cache.remove(key);
            log.info("[CACHE] removed from cache : {}", key);
        }

    }

    private Optional<RuleSpec> rule(Request request) {
        for (RuleSpec rule : this.rules) {
            if (rule.rule.matches(request)) {
                return Optional.of(rule);
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
        public final ResponseReviver reviver;

        public TimestampedResponse(long timestamp, ResponseReviver reviver) {
            this.timestamp = timestamp;
            this.reviver = reviver;
        }
    }

}
