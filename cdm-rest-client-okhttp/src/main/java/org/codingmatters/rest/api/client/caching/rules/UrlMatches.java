package org.codingmatters.rest.api.client.caching.rules;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CachingRule;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UrlMatches implements CachingRule {
    static public CachingRule regex(String regex) {
        return new UrlMatches(regex);
    }

    private final Predicate<String> predicate;

    public UrlMatches(String regex) {
        this.predicate = Pattern.compile(regex).asMatchPredicate();
    }

    @Override
    public boolean matches(Request request) {
        return this.predicate.test(request.url().url().getPath());
    }
}
