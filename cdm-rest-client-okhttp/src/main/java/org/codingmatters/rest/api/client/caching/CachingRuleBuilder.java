package org.codingmatters.rest.api.client.caching;

import org.codingmatters.rest.api.client.okhttp.HttpClientWrapper;

import java.util.LinkedList;
import java.util.List;

public class CachingRuleBuilder {

    private final List<RulesForKey> rulesForKeyList = new LinkedList<>();

    public RulesForKey key(CacheKey cacheKey) {
        RulesForKey rulesForKey = new RulesForKey(this, cacheKey);
        this.rulesForKeyList.add(rulesForKey);
        return rulesForKey;
    }

    public CachingHttpClientWrapper configure(CachingHttpClientWrapper cachingWrapper) {
        for (RulesForKey rulesForKey : this.rulesForKeyList) {
            rulesForKey.configure(cachingWrapper);
        }
        return cachingWrapper;
    }

    public HttpClientWrapper configure(HttpClientWrapper wrapper, CachingHttpClientWrapperCleaner cleaner) {
        return cleaner.register(this.configure(new CachingHttpClientWrapper(wrapper)));
    }

    static public class RulesForKey {
        private final List<CachingRule> rules = new LinkedList<>();

        private final CachingRuleBuilder cachingRuleBuilder;
        private final CacheKey cacheKey;

        private RulesForKey(CachingRuleBuilder cachingRuleBuilder, CacheKey cacheKey) {
            this.cachingRuleBuilder = cachingRuleBuilder;
            this.cacheKey = cacheKey;
        }

        public RulesForKey rule(CachingRule rule) {
            this.rules.add(rule);
            return this;
        }

        public CachingRuleBuilder done() {
            return this.cachingRuleBuilder;
        }

        private CachingHttpClientWrapper configure(CachingHttpClientWrapper cachingWrapper) {
            for (CachingRule rule : this.rules) {
                cachingWrapper.addCachingRule(rule, this.cacheKey);
            }
            return cachingWrapper;
        }
    }
}
