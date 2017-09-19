package org.codingmatters.rest.api.generator.client.support;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.ResponseDelegate;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class TestRequesterFactory implements RequesterFactory {

    public enum Method {GET, POST, PUT, PATCH, DELETE}

    static public class Call {
        private final Method method;
        private final TestRequester requester;

        public Call(Method method, TestRequester requester) {
            this.method = method;
            this.requester = requester;
        }

        public Method method() {
            return method;
        }

        public TestRequester requester() {
            return requester;
        }
    }

    private final HashMap<Method, LinkedList<TestResponseDeleguate>> nextResponses = new HashMap<>();
    private final LinkedList<Call> calls = new LinkedList<>();

    public TestRequesterFactory nextResponse(Method method, int code) {
        return this.nextResponse(method, code, null, null);
    }

    public TestRequesterFactory nextResponse(Method method, int code, byte[] body, Map<String, String> headers) {
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

    protected ResponseDelegate nextResponse(Method method, TestRequester requester) throws IOException {
        try {
            TestResponseDeleguate responseDeleguate = this.nextResponses.getOrDefault(method, new LinkedList<>()).pop();
            this.calls.add(new Call(method, requester));
            return responseDeleguate;
        } catch (NoSuchElementException e) {
            throw new IOException("no response was supposed to be returned for method " + method, e);
        }
    }

    public TestRequesterFactory called(Method method, TestRequester requester) {
        this.calls.add(new Call(method, requester));
        return this;
    }

    public LinkedList<Call> calls() {
        return calls;
    }
}
