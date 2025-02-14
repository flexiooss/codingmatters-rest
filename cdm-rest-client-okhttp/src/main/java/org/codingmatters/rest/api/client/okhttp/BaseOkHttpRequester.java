package org.codingmatters.rest.api.client.okhttp;

import okhttp3.*;
import org.codingmatters.rest.api.client.MultipartRequester;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.api.client.UrlProvider;
import org.codingmatters.rest.io.Content;
import org.codingmatters.rest.io.headers.HeaderEncodingHandler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class BaseOkHttpRequester implements Requester, MultipartRequester {

    private final HttpClientWrapper client;
    private final UrlProvider urlProvider;

    private String path = "/";
    private final TreeMap<String, String[]> parameters = new TreeMap<>();
    private final TreeMap<String, String[]> headers = new TreeMap<>();

    private final MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();


    public BaseOkHttpRequester(HttpClientWrapper client, UrlProvider urlProvider) {
        this.client = client;
        this.urlProvider = urlProvider;
    }

    @Override
    public MultipartRequester multipart(MediaType type) throws IOException {
        this.multipartBuilder.setType(type);
        return this;
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
        return this.post(contentType, Content.from(body));
    }

    @Override
    public ResponseDelegate post(String contentType, Content body) throws IOException {
        if(body == null) {
            body = Content.from(new byte[0]);
        }
        File temporaryFile = body.asTemporaryFile();
        Request request = this.prepareRequestBuilder().post(this.prepareBody(contentType, temporaryFile)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        } finally {
            temporaryFile.delete();
        }
    }

    public RequestBody prepareBody(String contentType, File body) {
        return RequestBody.create(body, MediaType.parse(contentType));
        //return RequestBody.create(MediaType.parse(contentType), body != null ? body : new byte[0]);
    }

    @Override
    public ResponseDelegate put(String contentType, byte[] body) throws IOException {
        return this.put(contentType, Content.from(body));
    }

    @Override
    public ResponseDelegate put(String contentType, Content body) throws IOException {
        if(body == null) {
            body = Content.from(new byte[0]);
        }
        File temporaryFile = body.asTemporaryFile();
        Request request = this.prepareRequestBuilder().put(this.prepareBody(contentType, temporaryFile)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        } finally {
            temporaryFile.delete();
        }
    }

    @Override
    public ResponseDelegate patch(String contentType, byte[] body) throws IOException {
        return this.patch(contentType, Content.from(body));
    }

    @Override
    public ResponseDelegate patch(String contentType, Content body) throws IOException {
        if(body == null) {
            body = Content.from(new byte[0]);
        }
        File temporaryFile = body.asTemporaryFile();
        Request request = this.prepareRequestBuilder().patch(this.prepareBody(contentType, temporaryFile)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        } finally {
            temporaryFile.delete();
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
        return this.delete(contentType, Content.from(body));
    }

    @Override
    public ResponseDelegate delete(String contentType, Content body) throws IOException {
        if(body == null) {
            body = Content.from(new byte[0]);
        }
        File temporaryFile = body.asTemporaryFile();
        Request request = this.prepareRequestBuilder().delete(this.prepareBody(contentType, temporaryFile)).build();
        try (Response response = this.client.execute(request)) {
            return new OkHttpResponseDelegate(response);
        } finally {
            temporaryFile.delete();
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
                url += queryParameterValue != null ? this.encode(queryParameterValue) : "null";
            }
        }

        Request.Builder result = new Request.Builder().url(url);

        for (Map.Entry<String, String[]> headerEntry : this.headers.entrySet()) {
            if(headerEntry.getValue() != null) {
                for( String value : headerEntry.getValue() ){
                    if( HeaderEncodingHandler.needEncoding( value )){
                        String name = headerEntry.getKey() + "*";
                        result.addHeader( name, HeaderEncodingHandler.encodeHeader( value ) );
//                        RFC 8187
//                        result.addHeader( headerEntry.getKey(), HeaderEncodingHandler.encodeHeader( value ) );
                    } else {
                        result.addHeader( headerEntry.getKey(), value );
                    }
                }
            }
        }
        return result;
    }

    private String encode(String str) throws UnsupportedEncodingException {
        if(str == null) return "null";
        return URLEncoder.encode(str, "UTF-8");
    }




    @Override
    public MultipartRequester formDataPart(String contentType, byte[] body, String name) {
        this.multipartBuilder.addFormDataPart(name, new String(body));
        return this;
    }

    @Override
    public MultipartRequester formDataPart(String contentType, Content body, String name) throws IOException {
        if (body == null) {
            body = Content.from(new byte[0]);
        }
        this.multipartBuilder.addFormDataPart(name, body.asString());
        return this;
    }

    @Override
    public MultipartRequester formDataPart(String contentType, File file, String name) throws IOException {
        this.multipartBuilder.addFormDataPart(name, file.getName(), RequestBody.create(file, MediaType.parse(contentType)));
        return this;
    }

    @Override
    public ResponseDelegate postMultiPart() throws IOException {
        final MultipartBody body = this.multipartBuilder.build();
        final Request.Builder post = this.prepareRequestBuilder()
                .post(body);
        try (Response response = this.client.execute(post.build())) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public ResponseDelegate putMultiPart() throws IOException {
        final MultipartBody body = this.multipartBuilder.build();
        final Request.Builder put = this.prepareRequestBuilder()
                .put(body);
        try (Response response = this.client.execute(put.build())) {
            return new OkHttpResponseDelegate(response);
        }
    }

    @Override
    public ResponseDelegate patchMultiPart() throws IOException {
        final MultipartBody body = this.multipartBuilder.build();
        final Request.Builder patch = this.prepareRequestBuilder()
                .patch(body);
        try (Response response = this.client.execute(patch.build())) {
            return new OkHttpResponseDelegate(response);
        }
    }


}
