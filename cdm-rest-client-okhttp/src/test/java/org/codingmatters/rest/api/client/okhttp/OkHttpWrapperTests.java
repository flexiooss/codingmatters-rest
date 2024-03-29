package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Request;
import org.codingmatters.rest.api.client.okhttp.exception.ConnectionTimeoutException;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

public class OkHttpWrapperTests {

    private static final long DELAY = 1000L;

    @Rule
    public UndertowResource server = new UndertowResource(
            exchange -> Thread.sleep(DELAY)
    );

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = UnknownHostException.class)
    public void whenUnknownHost__thenUnknownHostExceptionIsThrown() throws Exception {
        HttpClientWrapper client = OkHttpClientWrapper.build();
        client.execute(new Request.Builder().url("http://unknown.host.loc").build());
    }

    @Test(timeout = 1000L)
    public void connectionTimeout() throws Exception {
        HttpClientWrapper client = OkHttpClientWrapper.build(builder -> builder.connectTimeout(500, TimeUnit.MILLISECONDS)
        );

        this.exception.expect(ConnectionTimeoutException.class);
        this.exception.expectMessage(is("connection timed out"));

        client.execute(new Request.Builder().url("http://192.168.76.78").build());
    }

    @Test(timeout = 2000L)
    public void readTimeout() throws Exception {
        HttpClientWrapper client = OkHttpClientWrapper.build(builder -> builder.readTimeout(500, TimeUnit.MILLISECONDS)
        );

        this.exception.expect(SocketTimeoutException.class);
        this.exception.expectMessage(is(oneOf("Read timed out", "timeout")));
        client.execute(new Request.Builder().url(this.server.baseUrl()).build());
    }
}
