package org.codingmatters.rest.api.client.okhttp;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.UrlProvider;

public class OkHttpRequesterFactory implements RequesterFactory {
    private final HttpClientWrapper client;
    private final UrlProvider urlProvider;

    public OkHttpRequesterFactory(HttpClientWrapper client, UrlProvider urlProvider) {
        this.client = client;
        this.urlProvider = urlProvider;
    }

    @Override
    public Requester create() {
        return new OkHttpRequester(this.client, this.urlProvider);
    }
}
