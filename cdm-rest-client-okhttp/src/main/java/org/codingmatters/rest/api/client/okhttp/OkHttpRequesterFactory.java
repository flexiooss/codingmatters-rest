package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;

public class OkHttpRequesterFactory implements RequesterFactory {
    private final OkHttpClientWrapper client;

    public OkHttpRequesterFactory(OkHttpClient client) {
        this.client = OkHttpClientWrapper.from(client);
    }

    public OkHttpRequesterFactory(OkHttpClientWrapper client) {
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
