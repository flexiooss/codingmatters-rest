package org.codingmatters.rest.api.client.okhttp;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;

public class OkHttpRequesterFactory implements RequesterFactory {
    private final HttpClientWrapper client;

    public OkHttpRequesterFactory(HttpClientWrapper client) {
        this.client = client;
    }

    @Override
    public Requester forBaseUrl(String url) {
        while(url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return new OkHttpRequester(this.client, url);
    }
}
