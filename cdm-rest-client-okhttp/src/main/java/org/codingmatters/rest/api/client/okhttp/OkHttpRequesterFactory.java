package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;

public class OkHttpRequesterFactory implements RequesterFactory {
    private final OkHttpClient client;

    public OkHttpRequesterFactory(OkHttpClient client) {
        this.client = client;
    }


    @Override
    public Requester forBaseUrl(String url) {
        return new OkHttpRequester(this.client, url);
    }
}
