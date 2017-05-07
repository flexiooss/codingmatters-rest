package org.codingmatters.http;

import io.undertow.Undertow;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.codingmatters.http.api.Processor;
import org.codingmatters.http.api.RequestDeleguate;
import org.codingmatters.http.api.ResponseDeleguate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/7/17.
 */
public class UndertowResponseDeleguateTest extends AbstractUndertowTest {

    private Undertow server;
    private OkHttpClient client = new OkHttpClient();
    private Processor testProcessor;

    @Before
    public void setUp() throws Exception {
        int port;
        try(ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        this.server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(new CdmHttpUndertowHandler(this::process))
                .build();
        this.server.start();

        this.baseUrl = "http://localhost:" + port;
    }

    private void process(RequestDeleguate requestDeleguate, ResponseDeleguate responseDeleguate) throws IOException {
        this.testProcessor.process(requestDeleguate, responseDeleguate);
    }

    @After
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void contenType() throws Exception {
        this.testProcessor = (requestDeleguate, responseDeleguate) -> {responseDeleguate.contenType("yip/yop");};

        MediaType contentType = this.client.newCall(this.requestBuilder().get().build()).execute().body().contentType();
        assertThat(contentType.type(), is("yip"));
        assertThat(contentType.subtype(), is("yop"));
    }

    @Test
    public void status() throws Exception {
        this.testProcessor = (requestDeleguate, responseDeleguate) -> {responseDeleguate.status(201);};

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().code(), is(201));
    }

    @Test
    public void addHeader() throws Exception {
        this.testProcessor = (requestDeleguate, responseDeleguate) -> {responseDeleguate.addHeader("yip", "yop");};

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().header("yip"), is("yop"));
    }

    @Test
    public void payload() throws Exception {
        this.testProcessor = (requestDeleguate, responseDeleguate) -> {responseDeleguate.payload("yop yop", "utf-8");};

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().body().string(), is("yop yop"));
    }
}
