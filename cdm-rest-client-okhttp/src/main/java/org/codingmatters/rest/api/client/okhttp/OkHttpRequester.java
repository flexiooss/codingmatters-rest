package org.codingmatters.rest.api.client.okhttp;

public class OkHttpRequester extends BaseOkHttpRequester {

    public OkHttpRequester(HttpClientWrapper client, String url) {
        super(client, () -> url);
    }
}
