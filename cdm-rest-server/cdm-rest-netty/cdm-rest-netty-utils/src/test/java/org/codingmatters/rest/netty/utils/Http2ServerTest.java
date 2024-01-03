package org.codingmatters.rest.netty.utils;

import okhttp3.*;
import okhttp3.internal.http2.StreamResetException;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class Http2ServerTest extends AbstractHttpServerTest {

    protected OkHttpClient createClient() {
        return new OkHttpClient(new OkHttpClient.Builder()
                .readTimeout(60L, TimeUnit.SECONDS)
            .protocols(List.of(Protocol.H2_PRIOR_KNOWLEDGE))
        );
    }

    protected AbstratHttpServer createServer(int port) {
        return new Http2Server(NettyHttpConfig.builder().host("0.0.0.0").port(port).build(), this::handler);
    }


    @Override
    protected void asserHeaderTooLargeResponse(Response response) throws IOException {
        assertThat(response.code(), is(431));
        assertThat(response.header("content-type"), is(nullValue()));
        assertThat(response.body().string(), is(""));
    }

    @Override
    public void whenReadingMoreThan100MoUpload__thenKO() throws Exception {
        Assert.assertThrows(IOException.class, () -> super.whenReadingMoreThan100MoUpload__thenKO());
    }


    @Ignore
    @Override
    public void whenURIOver8k__then414() throws Exception {
    }

    @Test
    public void whenURIUnder32k__then200() throws Exception {
        String url = this.url;
        for (int i = 0; i * LINE.length() < 31512 ; i++) {
            url += "/" + LINE;
        }
        System.out.println("url size : " + (url.length() / 1024));
        Request.Builder request = new Request.Builder()
                .url(url).get();
        Response response = this.client.newCall(request.build()).execute();

        this.assertNominalResponse(response);
    }

    @Test
    public void whenURIOver32k__then200() throws Exception {
        String url = this.url;
        for (int i = 0; i * LINE.length() < 32000 ; i++) {
            url += "/" + LINE;
        }
        System.out.println("url size : " + (url.length() / 1024));
        Request.Builder request = new Request.Builder()
                .url(url).get();
        Response response = this.client.newCall(request.build()).execute();

        this.asserHeaderTooLargeResponse(response);
    }
}
