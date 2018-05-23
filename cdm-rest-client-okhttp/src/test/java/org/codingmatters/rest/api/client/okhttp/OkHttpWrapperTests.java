package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.codingmatters.rest.api.client.okhttp.exception.ConnectionTimeoutException;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;

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
        OkHttpClientWrapper client = OkHttpClientWrapper.build();
        client.execute(new Request.Builder().url("http://unknown.host.loc").build());
    }

    @Test(timeout = 1000L)
    public void connectionTimeout() throws Exception {
        OkHttpClientWrapper client = OkHttpClientWrapper.build(new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
        );

        this.exception.expect(ConnectionTimeoutException.class);
        this.exception.expectMessage(is("connection timed out"));

        client.execute(new Request.Builder().url("http://192.168.76.78").build());
    }

    @Test(timeout = 1000L)
    public void readTimeout() throws Exception {
        OkHttpClientWrapper client = OkHttpClientWrapper.build(new OkHttpClient.Builder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
        );

        this.exception.expect(SocketTimeoutException.class);
        this.exception.expectMessage(is("timeout"));
        client.execute(new Request.Builder().url(this.server.baseUrl()).build());
    }
}
