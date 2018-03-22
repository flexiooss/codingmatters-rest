package org.codingmatters.rest.api.client.test;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class TestRequesterFactory implements RequesterFactory {

    public enum Method {
        GET, HEAD, POST, PUT, PATCH, DELETE
    }

    static public class Call {
        private final Method method;
        private final String url;
        private final String path;
        private final HashMap<String, String[]> parameters;
        private final HashMap<String, String[]> headers;
        private final String requestContentType;
        private final byte [] requestBody;

        public Call(Method method, String url, String path, HashMap<String, String[]> parameters, HashMap<String, String[]> headers, String requestContentType, byte[] requestBody) {
            this.method = method;
            this.url = url;
            this.path = path;
            this.parameters = parameters;
            this.headers = headers;
            this.requestContentType = requestContentType;
            this.requestBody = requestBody;
        }

        public Method method() {
            return method;
        }

        public String url() {
            return url;
        }

        public String path() {
            return path;
        }

        public HashMap<String, String[]> parameters() {
            return parameters;
        }

        public HashMap<String, String[]> headers() {
            return headers;
        }

        public String requestContentType() {
            return requestContentType;
        }

        public byte[] requestBody() {
            return requestBody;
        }
    }

    private final HashMap<Method, LinkedList<TestResponseDeleguate>> nextResponses = new HashMap<>();
    private final LinkedList<Call> calls = new LinkedList<>();

    public TestRequesterFactory clear() {
        this.nextResponses.clear();
        this.calls.clear();
        return this;
    }

    public TestRequesterFactory nextResponse(Method method, int code) {
        return this.nextResponse(method, code, null, null);
    }

    public TestRequesterFactory nextResponse(Method method, int code, byte[] body) {
        return this.nextResponse(method, code, body, null);
    }

    public TestRequesterFactory nextResponse(Method method, int code, byte[] body, Map<String, String[]> headers) {
        if(! this.nextResponses.containsKey(method)) {
            this.nextResponses.put(method, new LinkedList<>());
        }
        this.nextResponses.get(method).add(new TestResponseDeleguate(code, body, headers));
        return this;
    }

    @Override
    public Requester forBaseUrl(String url) {
        return new TestRequester(url, this);
    }

    protected ResponseDelegate registeredNextResponse(Method method, TestRequester requester) throws IOException {
        try {
            TestResponseDeleguate responseDeleguate = this.nextResponses.getOrDefault(method, new LinkedList<>()).pop();
            return responseDeleguate;
        } catch (NoSuchElementException e) {
            throw new IOException("no response was supposed to be returned for method " + method, e);
        }
    }

    public TestRequesterFactory called(Call call) {
        this.calls.add(call);
        return this;
    }

    public LinkedList<Call> calls() {
        return calls;
    }
}
