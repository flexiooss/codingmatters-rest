package org.codingmatters.rest.api.client.okhttp;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.api.client.UrlProvider;
import org.codingmatters.rest.io.headers.HeaderEncodingHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class BaseOkHttpRequester implements Requester {

    private final HttpClientWrapper client;
    private final UrlProvider urlProvider;

    private String path = "/";
    private final TreeMap<String, String[]> parameters = new TreeMap<>();
    private final TreeMap<String, String[]> headers = new TreeMap<>();


    public BaseOkHttpRequester(HttpClientWrapper client, UrlProvider urlProvider) {
        this.client = client;
        this.urlProvider = urlProvider;
    }

    @Override
    public ResponseDelegate get() throws IOException {
        Request request = this.prepareRequestBuilder().get().build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public ResponseDelegate head() throws IOException {
        Request request = this.prepareRequestBuilder().head().build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    public ResponseDelegate post(String contentType, byte[] body) throws IOException {
        Request request = this.prepareRequestBuilder().post(this.prepareBody(contentType, body)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    public RequestBody prepareBody(String contentType, byte[] body) {
        return RequestBody.create(MediaType.parse(contentType), body != null ? body : new byte[0]);
    }

    @Override
    public ResponseDelegate put(String contentType, byte[] body) throws IOException {
        Request request = this.prepareRequestBuilder().put(this.prepareBody(contentType, body)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public ResponseDelegate patch(String contentType, byte[] body) throws IOException {
        Request request = this.prepareRequestBuilder().patch(this.prepareBody(contentType, body)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public ResponseDelegate delete() throws IOException {
        Request request = this.prepareRequestBuilder().delete().build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public ResponseDelegate delete(String contentType, byte[] body) throws IOException {
        Request request = this.prepareRequestBuilder().delete(this.prepareBody(contentType, body)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public Requester parameter(String name, String value) {
        return this.parameter(name, new String[] {value});
    }

    @Override
    public Requester parameter(String name, String[] value) {
        this.parameters.put(name, value);
        return this;
    }

    @Override
    public Requester parameter(String name, Iterable<String> value) {
        if(value != null) {
            LinkedList<String> params = new LinkedList<>();
            for (String v : value) {
                params.add(v);
            }

            return this.parameter(name, params.toArray(new String[params.size()]));
        }
        return this.parameter(name, new String[0]);
    }

    public Requester path(String path) {
        this.path = path;
        return this;
    }

    protected String path() {
        return path;
    }
    protected TreeMap<String, String[]> parameters() {
        return parameters;
    }

    @Override
    public Requester header(String name, String value) {
        return this.header(name, new String[] {value});
    }

    @Override
    public Requester header(String name, String [] value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public Requester header(String name, Iterable<String> value) {
        if(value != null) {
            LinkedList<String> params = new LinkedList<>();
            for (String v : value) {
                params.add(v);
            }

            return this.header(name, params.toArray(new String[params.size()]));
        } else {
            return this.header(name, new String[0]);
        }
    }

    @Override
    public Requester headerIfNot(String name, String[] value) {
        if(this.headers.get(name) == null || this.headers.get(name).length == 0) {
            return this.header(name, value);
        }
        return this;
    }


    private Request.Builder prepareRequestBuilder() throws UnsupportedEncodingException, IOException {
        String baseUrl = this.urlProvider.baseUrl();
        String path = this.path();

        String url = new UrlNormalizer(baseUrl, path).normalize();

        boolean first = true;
        for (Map.Entry<String, String[]> queryParameterEntry : this.parameters().entrySet()) {
            for (String queryParameterValue : queryParameterEntry.getValue()) {
                if(first) {
                    url += "?";
                    first = false;
                } else {
                    url += "&";
                }
                url += this.encode(queryParameterEntry.getKey());
                url += "=";
                url += queryParameterEntry.getValue() != null ? this.encode(queryParameterValue) : "null";
            }
        }

        Request.Builder result = new Request.Builder().url(url);

        for (Map.Entry<String, String[]> headerEntry : this.headers.entrySet()) {
            if(headerEntry.getValue() != null) {
                for( String value : headerEntry.getValue() ){
                    if( HeaderEncodingHandler.needEncoding( value )){
                        String name = headerEntry.getKey() + "*";
                        result.addHeader( name, HeaderEncodingHandler.encodeHeader( value ) );
                    } else {
                        result.addHeader( headerEntry.getKey(), value );
                    }
                }
            }
        }
        return result;
    }

    private String encode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF-8");
    }

}
