package org.codingmatters.rest.undertow.support;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class BehaviouralUndertowResourceTest {


    @Rule
    public BehaviouralUndertowResource undertow = new BehaviouralUndertowResource();


    private OkHttpClient client = new OkHttpClient();

    @Test
    public void whenNoBehaviourDefined__thenStatusIs404() throws Exception {
        Request request = new Request.Builder()
                .url(this.undertow.baseUrl())
                .get().build();

        assertThat(this.client.newCall(request).execute().code(), Matchers.is(404));
    }

    @Test
    public void whenNoBehaviourMatches__thenStatusIs404() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("POST"))
                .then(exchange -> exchange.setStatusCode(200));
        Request request = new Request.Builder()
                .url(this.undertow.baseUrl())
                .get().build();

        assertThat(this.client.newCall(request).execute().code(), Matchers.is(404));
    }

    @Test
    public void whenABehaviourMatches_andBehaviourSetsStatus__thenStatusIsSettedByBehaviour() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("GET"))
                .then(exchange -> exchange.setStatusCode(200));
        Request request = new Request.Builder()
                .url(this.undertow.baseUrl())
                .get().build();

        assertThat(this.client.newCall(request).execute().code(), Matchers.is(200));
    }

    @Test
    public void whenABehaviourMatches_andBehaviourDoesntSetStatus__thenStatusIs200() throws Exception {
        this.undertow
                .when(exchange -> exchange.getRequestMethod().toString().equalsIgnoreCase("GET"))
                .then(exchange -> {});
        Request request = new Request.Builder()
                .url(this.undertow.baseUrl())
                .get().build();

        assertThat(this.client.newCall(request).execute().code(), Matchers.is(200));
    }
}