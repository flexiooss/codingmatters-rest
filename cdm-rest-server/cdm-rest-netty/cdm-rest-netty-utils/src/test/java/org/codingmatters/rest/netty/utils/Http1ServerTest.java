package org.codingmatters.rest.netty.utils;

import okhttp3.OkHttpClient;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;

import java.util.concurrent.TimeUnit;

public class Http1ServerTest extends AbstractHttpServerTest {

    protected OkHttpClient createClient() {
        return new OkHttpClient(new OkHttpClient.Builder()
                .readTimeout(60L, TimeUnit.SECONDS)
        );
    }


    protected AbstratHttpServer createServer(int port) {
        return new Http1Server(NettyHttpConfig.builder().host("0.0.0.0").port(port).build(), this::handler);
    }
}
