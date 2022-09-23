package org.codingmatters.rest.api.client.caching.key;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.CacheKey;

import java.util.Optional;

public class HeaderKey implements CacheKey {

    static public CacheKey name(String name) {
        return new HeaderKey(name);
    }

    private final String name;

    public HeaderKey(String name) {
        this.name = name;
    }

    @Override
    public String key(Request request) {
        Optional<String> matching = request.headers().names().stream().filter(s -> s.toLowerCase().equals(this.name.toLowerCase())).findFirst();
        if(matching.isPresent()) {
            return request.header(matching.get());
        }
        return null;
    }
}
