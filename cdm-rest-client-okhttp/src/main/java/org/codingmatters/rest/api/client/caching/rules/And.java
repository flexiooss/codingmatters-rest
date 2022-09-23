package org.codingmatters.rest.api.client.caching.rules;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CachingRule;

public class And implements CachingRule {
    static public CachingRule all(CachingRule... rules) {
        return new And(rules);
    }

    private final CachingRule[] rules;

    private And(CachingRule ... rules) {
        this.rules = rules != null ? rules : new CachingRule[0];
    }

    @Override
    public boolean matches(Request request) {
        for (CachingRule rule : this.rules) {
            if(!rule.matches(request)) return false;
        }
        return true;
    }
}
