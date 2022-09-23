package org.codingmatters.rest.api.client.caching.key;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CacheKey;

public class CompoundKey implements CacheKey {
    static public CacheKey with(String separator, CacheKey ... keys) {
        return new CompoundKey(separator, keys);
    }

    private final String separator;
    private final CacheKey[] keys;

    public CompoundKey(String separator, CacheKey[] keys) {
        this.separator = separator;
        this.keys = keys;
    }

    @Override
    public String key(Request request) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.keys.length; i++) {
            if(i > 0) {
                result.append(this.separator);
            }
            result.append(this.keys[i].key(request));
        }
        return result.toString();
    }
}
