package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class OkHttpRequester extends Requester {
    private final OkHttpClient client;
    private final String baseUrl;

    public OkHttpRequester(OkHttpClient client, String url) {
        this.client = client;
        this.baseUrl = url;
    }

    @Override
    public ResponseDelegate get() throws IOException {
        Request request = this.prepareRequestBuilder().get().build();
        return new OkHttpResponseDelegate(this.client.newCall(request).execute());
    }

    private Request.Builder prepareRequestBuilder() throws UnsupportedEncodingException {
        String url = this.baseUrl + this.path();

        boolean firts = true;
        for (Map.Entry<String, String> queryParameterEntry : this.queryParameters().entrySet()) {
            if(firts) {
                url += "?";
                firts = false;
            } else {
                url += "&";
            }
            url += this.encode(queryParameterEntry.getKey());
            url += "=";
            url += queryParameterEntry.getValue() != null ? this.encode(queryParameterEntry.getValue()) : "null";
        }

        System.out.println("URL" + url);

        return new Request.Builder().url(url);
    }

    @Override
    public ResponseDelegate post() throws IOException {
        return null;
    }

    @Override
    public ResponseDelegate put() throws IOException {
        return null;
    }

    @Override
    public ResponseDelegate patch() throws IOException {
        return null;
    }

    @Override
    public ResponseDelegate delete() throws IOException {
        return null;
    }


    private String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }
}
