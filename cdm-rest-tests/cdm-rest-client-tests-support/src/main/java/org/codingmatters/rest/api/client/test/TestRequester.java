package org.codingmatters.rest.api.client.test;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import org.codingmatters.rest.api.client.MultipartRequester;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.api.client.UrlProvider;
import org.codingmatters.rest.io.Content;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static org.codingmatters.rest.api.client.test.TestRequesterFactory.Method.*;


public class TestRequester implements Requester, MultipartRequester {
    private final TestRequesterFactory factory;
    private final MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();


    private final UrlProvider urlProvider;
    private String path;
    private TreeMap<String, String[]> parameters = new TreeMap<>();
    private TreeMap<String, String[]> headers = new TreeMap<>();

    public TestRequester(UrlProvider urlProvider, TestRequesterFactory factory) {
        this.urlProvider = urlProvider;
        this.factory = factory;
    }

    public TestRequester(String url, TestRequesterFactory factory) {
        this(() -> url, factory);
    }


    @Override
    public ResponseDelegate get() throws IOException {
        return this.nextResponse(GET, null, null);
    }

    @Override
    public ResponseDelegate head() throws IOException {
        return this.nextResponse(HEAD, null, null);
    }

    @Override
    public ResponseDelegate post(String contentType, byte[] body) throws IOException {
        return this.nextResponse(POST, contentType, body);
    }

    @Override
    public ResponseDelegate post(String contentType, Content body) throws IOException {
        return this.post(contentType, body != null ? body.asBytes() : new byte[0]);
    }

    @Override
    public ResponseDelegate put(String contentType, byte[] body) throws IOException {
        return this.nextResponse(PUT, contentType, body);
    }

    @Override
    public ResponseDelegate put(String contentType, Content body) throws IOException {
        return this.put(contentType, body != null ? body.asBytes() : new byte[0]);
    }

    @Override
    public ResponseDelegate patch(String contentType, byte[] body) throws IOException {
        return this.nextResponse(PATCH, contentType, body);
    }

    @Override
    public ResponseDelegate patch(String contentType, Content body) throws IOException {
        return this.patch(contentType, body != null ? body.asBytes() : new byte[0]);
    }

    @Override
    public ResponseDelegate delete() throws IOException {
        return this.nextResponse(DELETE, null, null);
    }

    @Override
    public ResponseDelegate delete(String contentType, byte[] body) throws IOException {
        return this.nextResponse(DELETE, contentType, body);
    }

    @Override
    public ResponseDelegate delete(String contentType, Content body) throws IOException {
        return this.delete(contentType, body != null ? body.asBytes() : new byte[0]);
    }

    private ResponseDelegate nextResponse(TestRequesterFactory.Method method, String requestContentType, byte[] requestBody) throws IOException {
        try {
            this.factory.called(new TestRequesterFactory.Call(method, this.urlProvider.baseUrl(), this.path, new HashMap<>(this.parameters), new HashMap<>(this.headers), requestContentType, requestBody));
            return this.factory.registeredNextResponse(method, this);
        } catch (NoSuchElementException e) {
            throw new IOException("no response was supposed to be returned for method " + method, e);
        }
    }


    @Override
    public Requester parameter(String name, String value) {
        return this.parameter(name, new String[]{value});
    }

    @Override
    public Requester parameter(String name, String[] value) {
        this.parameters.put(name, value);
        return this;
    }

    @Override
    public Requester parameter(String name, Iterable<String> value) {
        if (value != null) {
            LinkedList<String> params = new LinkedList<>();
            for (String v : value) {
                params.add(v);
            }

            return this.parameter(name, params.toArray(new String[params.size()]));
        } else {
            return this.parameter(name, new String[0]);
        }
    }

    @Override
    public Requester header(String name, String value) {
        return this.header(name, new String[]{value});
    }

    @Override
    public Requester header(String name, String[] value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public Requester headerIfNot(String name, String[] value) {
        if (this.headers.get(name) == null || this.headers.get(name).length == 0) {
            return this.header(name, value);
        }
        return this;
    }

    @Override
    public Requester header(String name, Iterable<String> value) {
        if (value != null) {
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
    public Requester path(String path) {
        this.path = path;
        return this;
    }


//    ################ MULTIPART #####################

    private ResponseDelegate nextMultiPartResponse(TestRequesterFactory.Method method) throws IOException {
        final MultipartBody body = this.multipartBuilder.build();
        try {
            final String contentType = body.contentType().toString();
            final Buffer buffer = new Buffer();
            body.writeTo(buffer);
            this.factory.called(new TestRequesterFactory.Call(method, this.urlProvider.baseUrl(), this.path, new HashMap<>(this.parameters), new HashMap<>(this.headers), contentType, buffer.readByteArray()));
            return this.factory.registeredNextResponse(method, this);
        } catch (NoSuchElementException e) {
            throw new IOException("no response was supposed to be returned for method " + method, e);
        }
    }

    @Override
    public MultipartRequester multipart(MediaType type) {
        this.multipartBuilder.setType(type);
        return this;
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
        return this.nextMultiPartResponse(POST);
    }

    @Override
    public ResponseDelegate putMultiPart() throws IOException {
        return this.nextMultiPartResponse(PUT);
    }

    @Override
    public ResponseDelegate patchMultiPart() throws IOException {
        return this.nextMultiPartResponse(PATCH);
    }

}
