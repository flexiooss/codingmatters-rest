package org.codingmatters.rest.api.client.caching;

import okhttp3.Request;
import org.codingmatters.rest.api.client.caching.key.CompoundKey;
import org.codingmatters.rest.api.client.caching.key.HeaderKey;
import org.codingmatters.rest.api.client.caching.key.QueryKey;
import org.codingmatters.rest.api.client.caching.key.UrlPathKey;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.Matchers.*;

public class CacheKeyTest {
    @Test
    public void whenHeaderKey__thenHeaderValue() throws Exception {
        assertThat(
                HeaderKey.name("plop").key(new Request.Builder().header("plop", "plip").get().url("https://some.where/over/the/rain.bow").build()),
                is("plip")
        );
    }

    @Test
    public void whenHeaderKey__thenCaseInsensitive() throws Exception {
        assertThat(
                HeaderKey.name("X-Request-ID").key(new Request.Builder().header("X-Request-ID", "plip").get().url("https://some.where/over/the/rain.bow").build()),
                is("plip")
        );
        assertThat(
                HeaderKey.name("X-Request-ID").key(new Request.Builder().header("x-request-id", "plip").get().url("https://some.where/over/the/rain.bow").build()),
                is("plip")
        );
        assertThat(
                HeaderKey.name("X-Request-ID").key(new Request.Builder().header("X-REQUEST-ID", "plip").get().url("https://some.where/over/the/rain.bow").build()),
                is("plip")
        );
    }

    @Test
    public void given__whenUrlPathKey__thenPath() throws Exception {
        assertThat(
                UrlPathKey.path().key(new Request.Builder().header("plop", "plip").get().url("https://some.where/over/the/rain.bow").build()),
                is("/over/the/rain.bow")
        );
    }

    @Test
    public void whenCompoundKey__thenKeyValuesJoinedWithSeparator() throws Exception {
        assertThat(
                CompoundKey.with("&", request -> "1", request -> "2", request -> "3").key(new Request.Builder().header("plop", "plip").get().url("https://some.where/over/the/rain.bow").build()),
                is("1&2&3")
        );
    }

    @Test
    public void given__whenQueryKey__thenQuery() throws Exception {
        assertThat(
                QueryKey.query().key(new Request.Builder().header("plop", "plip").get().url("https://some.where/over/the/rain.bow?p1=v1&p2=v2").build()),
                is("p1=v1&p2=v2")
        );
    }
}