package org.codingmatters.rest.netty.utils;

import io.netty.handler.codec.http.*;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@Ignore
public class Http2VsHttp1ConnectionOverheadTest {


    private Http2Server server;
    private String url;

    @Before
    public void setUp() throws Exception {
        ServerSocket freePortSocket = new ServerSocket(0);
        int port = freePortSocket.getLocalPort();
        freePortSocket.close();
        this.url = "http://localhost:" + port;

        this.server = new Http2Server(NettyHttpConfig.builder().host("0.0.0.0").port(port).build(), this::handler);
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

    private OkHttpClient h1Client = new OkHttpClient(new OkHttpClient.Builder()
                .readTimeout(60L,TimeUnit.SECONDS)
            .protocols(List.of(Protocol.HTTP_1_1))
            );
    private OkHttpClient h2Client = new OkHttpClient(new OkHttpClient.Builder()
                .readTimeout(60L,TimeUnit.SECONDS)
            .protocols(List.of(Protocol.H2_PRIOR_KNOWLEDGE))
            );

    @Test
    public void given__when__then() throws Exception {
        int loop = 100;

        // warmup
        for (int i = 0; i < 50; i++) {
            this.h2Client.newCall(new Request.Builder().url(this.url).get().build()).execute();
        }


        long h2Start = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            this.h2Client.newCall(new Request.Builder().url(this.url).get().build()).execute();
        }
        long h2Ellapsed = System.currentTimeMillis() - h2Start;

        //warmup

        for (int i = 0; i < 50; i++) {
            this.h1Client.newCall(new Request.Builder().url(this.url).get().build()).execute();
        }
        long h1Start = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            this.h1Client.newCall(new Request.Builder().url(this.url).get().build()).execute();
        }
        long h1Ellapsed = System.currentTimeMillis() - h1Start;

        assertThat(h1Ellapsed, is(greaterThan(h2Ellapsed * 2 / 3)));
    }
}
