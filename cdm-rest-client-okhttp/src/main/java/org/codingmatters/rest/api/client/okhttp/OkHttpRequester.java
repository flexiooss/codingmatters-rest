package org.codingmatters.rest.api.client.okhttp;

import org.codingmatters.rest.api.client.MultipartRequester;
import org.codingmatters.rest.api.client.UrlProvider;

public class OkHttpRequester extends BaseOkHttpRequester {

    @Deprecated
    public OkHttpRequester(HttpClientWrapper client, String url) {
        this(client, () -> url);
    }

    public OkHttpRequester(HttpClientWrapper client, UrlProvider urlProvider) {
        super(client, urlProvider);
    }
}
