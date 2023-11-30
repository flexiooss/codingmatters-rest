package org.codingmatters.rest.netty.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HttpServerTest {

    public static final String LINE = "12345678901234567890123456789012345678901234567890";
    private OkHttpClient client = new OkHttpClient();
    private HttpServer server;
    private String url;
    @Before
    public void setUp() throws Exception {
        ServerSocket freePortSocket = new ServerSocket(0);
        int port = freePortSocket.getLocalPort();
        freePortSocket.close();
        this.url = "http://localhost:" + port;
        this.server = HttpServer.server(NettyHttpConfig.builder().host("0.0.0.0").port(port).build(), this::handler);
        this.server.start();
    }

    private HttpRequestHandler handler(String host, int port) {
        return new HttpRequestHandler() {
            @Override
            protected HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body) {
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                response.setStatus(OK);
                byte[] bytes = "Test response : OK.".getBytes(StandardCharsets.UTF_8);
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                response.content().writeBytes(bytes);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                return response;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        this.server.shutdown();
        this.server.awaitTermination();
    }

    @Test
    public void whenNominal__theOK() throws Exception {
        Call call = this.client.newCall(new Request.Builder()
                .url(this.url + "/path/to/here")
                .build());
//        call.timeout().timeout(30, TimeUnit.SECONDS);
        Response response = call.execute();

        assertThat(response.code(), is(200));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Test response : OK."));
    }

    @Test
    public void whenLessThan8kHeaders__thenOK() throws Exception {
        String line = "12345678901234567890123456789012345678901234567890";

        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here");
        for (int i = 0; i * line.length() < 6500 ; i++) {
            request.header("header-" + i, line);
        }
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Test response : OK."));

    }

    @Test
    public void whenLessThan16kHeaders__thenOK() throws Exception {
        String line = "12345678901234567890123456789012345678901234567890";

        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here");
        for (int i = 0; i * line.length() < 13000 ; i++) {
            request.header("header-" + i, line);
        }
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Test response : OK."));

    }

    @Test
    public void whenOver16kHeaders__then431() throws Exception {
        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here");
        for (int i = 0; i * LINE.length() < 30000 ; i++) {
            request.header("header-" + i, LINE);
        }
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.code(), is(431));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Request Header Fields Too Large"));
    }

    @Test
    public void whenURILessThan4k__thenOK() throws Exception {
        String url = this.url;
        for (int i = 0; i * LINE.length() < 3500 ; i++) {
            url += "/" + LINE;
        }
        Request.Builder request = new Request.Builder()
                .url(url);
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Test response : OK."));
    }

    @Test
    public void whenURILessThan8k__thenOK() throws Exception {
        String url = this.url;
        for (int i = 0; i * LINE.length() < 6500 ; i++) {
            url += "/" + LINE;
        }
        Request.Builder request = new Request.Builder()
                .url(url);
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Test response : OK."));
    }

    @Test
    public void whenURIOver8k__then414() throws Exception {
        String url = this.url;
        for (int i = 0; i * LINE.length() < 13000 ; i++) {
            url += "/" + LINE;
        }
        Request.Builder request = new Request.Builder()
                .url(url);
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.code(), is(414));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("URI Too Long"));
    }
}