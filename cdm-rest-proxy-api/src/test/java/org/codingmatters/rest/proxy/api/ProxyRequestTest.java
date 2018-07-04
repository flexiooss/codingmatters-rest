package org.codingmatters.rest.proxy.api;

import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.api.client.test.TestRequesterFactory;
import org.codingmatters.rest.tests.api.TestRequestDeleguate;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.codingmatters.rest.api.client.test.TestRequesterFactory.Method.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ProxyRequestTest {

    private TestRequesterFactory requesterFactory = new TestRequesterFactory();

    @Test
    public void methods() throws Exception {
        for (RequestDelegate.Method method : RequestDelegate.Method.values()) {
            if(method.equals(RequestDelegate.Method.UNIMPLEMENTED)) continue;

            this.requesterFactory.clear().nextResponse(this.factoryMethod(method), 200);

            ProxyRequest.from(TestRequestDeleguate.request(method, "https://some/where").build())
                    .to(requesterFactory.forBaseUrl("https://else/where"));

            TestRequesterFactory.Call call = this.requesterFactory.calls().get(0);
            assertThat("Method " + method, call.method(), is(this.factoryMethod(method)));
        }
    }

    @Test
    public void response() throws Exception {
        Map<String, String[]> headers = new TreeMap<>();
        headers.put("key", new String[] {"v1", "v2"});

        this.requesterFactory.clear().nextResponse(GET, 200, "my content is rich".getBytes(), headers);

        ResponseDelegate response = ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.GET, "https://some/where").build())
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(response.code(), is(200));
        assertThat(response.body(), is("my content is rich".getBytes()));
        assertThat(response.header("key"), is(arrayContaining("v1", "v2")));
    }

    @Test
    public void requestPassBy_post() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).method(), is(POST));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(arrayContaining("qv1", "qv2")));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(arrayContaining("hv1", "hv2")));
        assertThat(this.requesterFactory.calls().get(0).requestContentType(), is("text/plain"));
        assertThat(this.requesterFactory.calls().get(0).requestBody(), is("my request content is rich".getBytes()));
    }

    @Test
    public void requestPassBy_patch() throws Exception {
        this.requesterFactory.clear().nextResponse(PATCH, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.PATCH, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).method(), is(PATCH));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(arrayContaining("qv1", "qv2")));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(arrayContaining("hv1", "hv2")));
        assertThat(this.requesterFactory.calls().get(0).requestContentType(), is("text/plain"));
        assertThat(this.requesterFactory.calls().get(0).requestBody(), is("my request content is rich".getBytes()));
    }

    @Test
    public void requestPassBy_put() throws Exception {
        this.requesterFactory.clear().nextResponse(PUT, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.PUT, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).method(), is(PUT));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(arrayContaining("qv1", "qv2")));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(arrayContaining("hv1", "hv2")));
        assertThat(this.requesterFactory.calls().get(0).requestContentType(), is("text/plain"));
        assertThat(this.requesterFactory.calls().get(0).requestBody(), is("my request content is rich".getBytes()));
    }

    @Test
    public void requestPassBy__withNewHeader() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .withHeader("h2", "h2v1", "h2v2")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(arrayContaining("hv1", "hv2")));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h2"), is(arrayContaining("h2v1", "h2v2")));
    }

    @Test
    public void requestPassBy__withHeaderAddedValues() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .build())
                .withAddedHeader("h", "hv3", "hv4")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        System.out.println(Arrays.asList(this.requesterFactory.calls().get(0).headers().get("h")));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(arrayContaining("hv1", "hv2", "hv3", "hv4")));
    }

    @Test
    public void requestPassBy__withHeaderReplacedValues() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .build())
                .withHeader("h", "hv3", "hv4")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(arrayContaining("hv3", "hv4")));
    }

    @Test
    public void requestPassBy__withHeaderRemoved() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .build())
                .withoutHeader("h")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).headers().get("h"), is(nullValue()));
    }


    @Test
    public void requestPassBy__withNewQueryParameters() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addHeader("h", "hv1", "hv2")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .withQueryParameters("q2", "q2v1", "q2v2")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(arrayContaining("qv1", "qv2")));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q2"), is(arrayContaining("q2v1", "q2v2")));
    }

    @Test
    public void requestPassBy__withQueryParametersAddedValues() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .withAddedQueryParameters("q", "qv3", "qv4")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(arrayContaining("qv1", "qv2", "qv3", "qv4")));
    }

    @Test
    public void requestPassBy__withQueryParametersReplacedValues() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .withQueryParameters("q", "qv3", "qv4")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(arrayContaining("qv3", "qv4")));
    }

    @Test
    public void requestPassBy__withQueryParametersRemoved() throws Exception {
        this.requesterFactory.clear().nextResponse(POST, 200);

        ProxyRequest
                .from(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some/where")
                        .addQueryParam("q", "qv1", "qv2")
                        .contentType("text/plain")
                        .payload(new ByteArrayInputStream("my request content is rich".getBytes()))
                        .build())
                .withoutQueryParameters("q")
                .to(requesterFactory.forBaseUrl("https://else/where"));

        assertThat(this.requesterFactory.calls(), hasSize(1));
        assertThat(this.requesterFactory.calls().get(0).parameters().get("q"), is(nullValue()));
    }

    private TestRequesterFactory.Method factoryMethod(RequestDelegate.Method method) {
        return TestRequesterFactory.Method.valueOf(method.name());
    }


}