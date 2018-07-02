package org.codingmatters.rest.api.client.okhttp;

import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.undertow.support.BehaviouralUndertowResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OkHttpRequesterTest {

    @Rule
    public BehaviouralUndertowResource undertow = new BehaviouralUndertowResource();

    private OkHttpClientWrapper client = OkHttpClientWrapper.build();
    private Requester requester;

    @Before
    public void setUp() throws Exception {
        this.requester = new OkHttpRequester(client, this.undertow.baseUrl());
    }

    @Test
    public void get() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("GET"))
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.get().code(), is(200));
    }

    @Test
    public void post() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("POST"))
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.post("text/plain", "hello".getBytes()).code(), is(200));
    }

    @Test
    public void put() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("PUT"))
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.put("text/plain", "hello".getBytes()).code(), is(200));
    }

    @Test
    public void patch() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("PATCH"))
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.patch("text/plain", "hello".getBytes()).code(), is(200));
    }

    @Test
    public void delete() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("DELETE"))
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.delete().code(), is(200));
    }

    @Test
    public void deleteWithBody() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("DELETE"))
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.delete("text/plain", "hello".getBytes()).code(), is(200));
    }

    @Test
    public void oneUniqueRequestParameter() throws Exception {
        this.undertow
                .when(exchange ->
                        exchange.getQueryParameters().size() == 1 &&
                                exchange.getQueryParameters().get("p").size() == 1 &&
                                exchange.getQueryParameters().get("p").getFirst().equals("v")
                )
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester.parameter("p", "v").get().code(), is(200));
    }

    @Test
    public void manyUniqueRequestParameter() throws Exception {
        this.undertow
                .when(exchange ->
                        exchange.getQueryParameters().size() == 3 &&
                                exchange.getQueryParameters().get("p1").size() == 1 &&
                                exchange.getQueryParameters().get("p2").size() == 1 &&
                                exchange.getQueryParameters().get("p3").size() == 1 &&
                                exchange.getQueryParameters().get("p1").getFirst().equals("v") &&
                                exchange.getQueryParameters().get("p2").getFirst().equals("v") &&
                                exchange.getQueryParameters().get("p2").getFirst().equals("v")
                )
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester
                    .parameter("p1", "v")
                    .parameter("p2", "v")
                    .parameter("p3", "v")
                    .get().code(),
                is(200)
        );
    }

    @Test
    public void oneHeader() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestHeaders().get("X-Test").getFirst().equals("v")
                )
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester
                    .header("X-Test", "v")
                .get().code(),
                is(200)
        );
    }

    @Test
    public void oneHeaderWithTwoValues() throws Exception {
        this.undertow
                .when(exchange ->
                        exchange.getRequestHeaders().get("X-Test").get(0).equals("v1") &&
                        exchange.getRequestHeaders().get("X-Test").get(1).equals("v2")
                )
                .then(exchange -> exchange.setStatusCode(200));

        assertThat(this.requester
                    .header("X-Test", new String[]{"v1", "v2"})
                .get().code(),
                is(200)
        );
    }
}