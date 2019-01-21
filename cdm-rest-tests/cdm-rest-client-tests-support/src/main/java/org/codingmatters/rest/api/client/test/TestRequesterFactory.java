package org.codingmatters.rest.api.client.test;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.client.RequesterFactory;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.api.client.UrlProvider;

import java.io.IOException;
import java.util.*;

public class TestRequesterFactory implements RequesterFactory {

    private final UrlProvider urlProvider;

    public TestRequesterFactory(UrlProvider urlProvider) {
        this.urlProvider = urlProvider;
    }

    public enum Method {
        GET, HEAD, POST, PUT, PATCH, DELETE
    }

    static public class Call {
        private final Method method;
        private final String url;
        private final String path;
        private final HashMap<String, List<String>> parameters;
        private final HashMap<String, List<String>> headers;
        private final String requestContentType;
        private final byte [] requestBody;

        public Call(Method method, String url, String path, HashMap<String, String[]> parameters, HashMap<String, String[]> headers, String requestContentType, byte[] requestBody) {
            this.method = method;
            this.url = url;
            this.path = path;
            this.parameters = this.arrayHashMapToListHashmap(parameters);
            this.headers = this.arrayHashMapToListHashmap(headers);
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
            return this.listToArrayHashMap(parameters);
        }

        public HashMap<String, String[]> headers() {
            return this.listToArrayHashMap(this.headers);
        }

        public String requestContentType() {
            return requestContentType;
        }

        public byte[] requestBody() {
            return requestBody;
        }

        private HashMap<String, List<String>> arrayHashMapToListHashmap(HashMap<String, String[]> arrayHashMap) {
            HashMap<String, List<String>> listHashMap = new HashMap<>();
            arrayHashMap.forEach(
                    (k,v) -> listHashMap.put(k, Arrays.asList(v))
            );
            return listHashMap;
        }

        private HashMap<String, String[]> listToArrayHashMap(HashMap<String, List<String>> listHashMap) {
            HashMap<String, String[]> arrayHashMap = new HashMap<>();
            listHashMap.forEach(
                    (k,v) -> arrayHashMap.put(k, v.stream().toArray(String[]::new))
            );
            return arrayHashMap;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Call call = (Call) o;
            return method == call.method &&
                    Objects.equals(url, call.url) &&
                    Objects.equals(path, call.path) &&
                    Objects.equals(parameters, call.parameters) &&
                    Objects.equals(headers, call.headers) &&
                    Objects.equals(requestContentType, call.requestContentType) &&
                    Arrays.equals(requestBody, call.requestBody);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(method, url, path, parameters, headers, requestContentType);
            result = 31 * result + Arrays.hashCode(requestBody);
            return result;
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
    public Requester create() {
        return new TestRequester(this.urlProvider, this);
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

    public Optional<Call> lastCall() {
        if(! calls.isEmpty()) {
            return Optional.of(this.calls.getLast());
        } else {
            return Optional.empty();
        }
    }
}
