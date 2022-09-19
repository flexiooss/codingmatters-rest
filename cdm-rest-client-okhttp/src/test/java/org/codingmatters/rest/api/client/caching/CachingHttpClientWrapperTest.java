package org.codingmatters.rest.api.client.caching;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.caching.rules.UrlMatches;
import org.codingmatters.rest.api.client.okhttp.HttpClientWrapper;
import org.codingmatters.rest.api.client.okhttp.OkHttpClientWrapper;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class CachingHttpClientWrapperTest {
    @Rule
    public UndertowResource undertow = new UndertowResource(this::serverCalled);

    private final AtomicInteger callCount = new AtomicInteger(0);

    private void serverCalled(HttpServerExchange exchange) {
        this.callCount.incrementAndGet();
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(new HttpString("counter"), this.callCount.get());
    }

    private HttpClientWrapper httpClientWrapper = OkHttpClientWrapper.build();

    @Test
    public void givenGetOnCachedUrl__whenTwoRequestsWithTwoDifferentRequestIds__thenServerCalledTwice_andTwoDifferentResponses() throws Exception {
        CachingHttpClientWrapper caching = new CachingHttpClientWrapper(this.httpClientWrapper)
                .addCachingRule(UrlMatches.regex("/cached"), CacheKey.REQUEST_ID_AND_PATH);

        Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                        .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));

        response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "42")
                .get().build());
        assertThat(this.callCount.get(), is(2));
        assertThat(response.header("counter"), is("2"));
    }

    @Test
    public void givenGetOnCachedUrl__whenTwoRequestWithSameRequestIds__thenServerCalledOnce_andSameResponse() throws Exception {
        CachingHttpClientWrapper caching = new CachingHttpClientWrapper(this.httpClientWrapper)
                .addCachingRule(UrlMatches.regex("/cached"), CacheKey.REQUEST_ID_AND_PATH);

        Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                        .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));

        response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));
    }

    @Test
    public void givenGetOnNotCachedUrl__whenTwoRequestsTwoWithSameRequestIds__thenServerCalledTwice_andTwoDifferentResponses() throws Exception {
        CachingHttpClientWrapper caching = new CachingHttpClientWrapper(this.httpClientWrapper)
                .addCachingRule(UrlMatches.regex("/cached"), CacheKey.REQUEST_ID_AND_PATH);

        Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/not-cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));

        response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/not-cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(2));
        assertThat(response.header("counter"), is("2"));
    }


    @Test
    public void givenGetOnCachedUrl__whenSameRequestsWithTwoDifferentUrl__thenServerCalledTwice_andTwoDifferentResponses() throws Exception {
        CachingHttpClientWrapper caching = new CachingHttpClientWrapper(this.httpClientWrapper)
                .addCachingRule(UrlMatches.regex("/cached"), CacheKey.REQUEST_ID_AND_PATH);

        Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));

        response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached/elsewhere")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(2));
        assertThat(response.header("counter"), is("2"));
    }


    @Test
    public void givenGetOnCachedUrl__whenRegisteredOnCleaner_andCacheTimeElapsed__thenServerCalledTwice_andTwoDifferentResponses() throws Exception {
        CachingHttpClientWrapper caching = new CachingHttpClientWrapper(this.httpClientWrapper)
                .addCachingRule(UrlMatches.regex("/cached"), CacheKey.REQUEST_ID_AND_PATH);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        CachingHttpClientWrapperCleaner cleaner = new CachingHttpClientWrapperCleaner(scheduler, 500L, 1000L);
        cleaner.register(caching);
        cleaner.start();
        try {
            Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                    .header("X-Request-ID", "12")
                    .get().build());
            assertThat(this.callCount.get(), is(1));
            assertThat(response.header("counter"), is("1"));

            Thread.sleep(2000L);

            response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                    .header("X-Request-ID", "12")
                    .get().build());
            assertThat(this.callCount.get(), is(2));
            assertThat(response.header("counter"), is("2"));
        } finally {
            cleaner.stop();
            scheduler.shutdownNow();
        }
    }


    @Test
    public void givenConfiguredWithBuilder_andGetOnCachedUrl__whenTwoRequestsWithTwoDifferentRequestIds__thenServerCalledTwice_andTwoDifferentResponses() throws Exception {
        HttpClientWrapper caching = new CachingRuleBuilder()
                .key(CacheKey.REQUEST_ID_AND_PATH).rule(UrlMatches.regex("/cached")).done()
                .configure(this.httpClientWrapper);

        Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));

        response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "42")
                .get().build());
        assertThat(this.callCount.get(), is(2));
        assertThat(response.header("counter"), is("2"));
    }

    @Test
    public void givenConfiguredWithBuilder_andGetOnCachedUrl__whenTwoRequestWithSameRequestIds__thenServerCalledOnce_andSameResponse() throws Exception {
        HttpClientWrapper caching = new CachingRuleBuilder()
                .key(CacheKey.REQUEST_ID_AND_PATH).rule(UrlMatches.regex("/cached")).done()
                .configure(this.httpClientWrapper);

        Response response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));

        response = caching.execute(new Request.Builder().url(this.undertow.baseUrl() + "/cached")
                .header("X-Request-ID", "12")
                .get().build());
        assertThat(this.callCount.get(), is(1));
        assertThat(response.header("counter"), is("1"));
    }
}