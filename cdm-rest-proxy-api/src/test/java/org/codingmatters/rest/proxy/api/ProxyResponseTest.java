package org.codingmatters.rest.proxy.api;

import org.codingmatters.rest.tests.api.TestResponseDeleguate;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ProxyResponseTest {

    @Test
    public void status() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();

        ProxyResponse.from(this.response(200, new byte[0], new TreeMap<>())).to(response);

        assertThat(response.status(), is(200));
    }

    @Test
    public void modifiedStatus() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();

        ProxyResponse.from(this.response(200, new byte[0], new TreeMap<>()))
                .withStatus(201)
                .to(response);

        assertThat(response.status(), is(201));
    }

    @Test
    public void body() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();

        ProxyResponse.from(this.response(200, "proxied".getBytes(), new TreeMap<>())).to(response);

        assertThat(response.payload(), is("proxied".getBytes()));
    }

    @Test
    public void modifiedBody() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();

        ProxyResponse.from(this.response(200, "proxied".getBytes(), new TreeMap<>()))
                .withBody("modified".getBytes())
                .to(response);

        assertThat(response.payload(), is("modified".getBytes()));
    }

    @Test
    public void contentType() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();

        ProxyResponse.from(this.response(200, "proxied".getBytes(), new TreeMap<>(), "text/html")).to(response);

        assertThat(response.contentType(), is("text/html"));
    }

    @Test
    public void modifiedContentType() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();

        ProxyResponse.from(this.response(200, "proxied".getBytes(), new TreeMap<>(), "text/html"))
                .withContentType("text/plain")
                .to(response);

        assertThat(response.contentType(), is("text/plain"));
    }

    @Test
    public void headers() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();
        TreeMap<String, String[]> headers = new TreeMap<>();
        headers.put("h", new String [] {"v1", "v2"});

        ProxyResponse.from(this.response(200, new byte[0], headers)).to(response);

        assertThat(response.headers().get("h"), is(arrayContaining("v1", "v2")));
    }

    @Test
    public void addHeaders() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();
        TreeMap<String, String[]> headers = new TreeMap<>();
        headers.put("h", new String [] {"v1", "v2"});

        ProxyResponse.from(this.response(200, new byte[0], headers))
                .withHeader("h2", "v3", "v4")
                .to(response);

        assertThat(response.headers().get("h"), is(arrayContaining("v1", "v2")));
        assertThat(response.headers().get("h2"), is(arrayContaining("v3", "v4")));
    }

    @Test
    public void replaceHeaders() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();
        TreeMap<String, String[]> headers = new TreeMap<>();
        headers.put("h", new String [] {"v1", "v2"});

        ProxyResponse.from(this.response(200, new byte[0], headers))
                .withHeader("h", "v3", "v4")
                .to(response);

        assertThat(response.headers().get("h"), is(arrayContaining("v3", "v4")));
    }

    @Test
    public void addHeaderValues() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();
        TreeMap<String, String[]> headers = new TreeMap<>();
        headers.put("h", new String [] {"v1", "v2"});

        ProxyResponse.from(this.response(200, new byte[0], headers))
                .addHeaderValues("h", "v3", "v4")
                .to(response);

        assertThat(response.headers().get("h"), is(arrayContaining("v1", "v2", "v3", "v4")));
    }

    @Test
    public void removeHeaders() throws Exception {
        TestResponseDeleguate response = new TestResponseDeleguate();
        TreeMap<String, String[]> headers = new TreeMap<>();
        headers.put("h", new String [] {"v1", "v2"});

        ProxyResponse.from(this.response(200, new byte[0], headers))
                .removeHeaders("h")
                .to(response);

        assertThat(response.headers().get("h"), is(nullValue()));
    }




    private org.codingmatters.rest.api.client.test.TestResponseDeleguate response(int code, byte[] body, Map<String, String[]> headers) {
        return new org.codingmatters.rest.api.client.test.TestResponseDeleguate(code, body, headers);
    }

    private org.codingmatters.rest.api.client.test.TestResponseDeleguate response(int code, byte[] body, Map<String, String[]> headers, String contentType) {
        return new org.codingmatters.rest.api.client.test.TestResponseDeleguate(code, body, headers, contentType);
    }
}