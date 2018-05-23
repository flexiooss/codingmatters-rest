package org.codingmatters.rest.api.client.okhttp;

public class OkHttpRequester extends BaseOkHttpRequester {

    public OkHttpRequester(OkHttpClientWrapper client, String url) {
        super(client, () -> url);
    }
}
