package org.codingmatters.rest.netty.utils;

import io.netty.handler.codec.http.*;
import okhttp3.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class AbstractHttpServerTest {

    public static final String LINE = "12345678901234567890123456789012345678901234567890";

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    protected OkHttpClient client;
    private AbstratHttpServer server;
    protected String url;

    abstract protected OkHttpClient createClient();
    abstract protected AbstratHttpServer createServer(int port);

    @Before
    public void setUp() throws Exception {
        this.client = this.createClient();

        ServerSocket freePortSocket = new ServerSocket(0);
        int port = freePortSocket.getLocalPort();
        freePortSocket.close();
        this.url = "http://localhost:" + port;

        this.server = this.createServer(port);
        this.server.start();
    }



    private final AtomicReference<HttpRequestHandler> handler = new AtomicReference<>(
        new HttpRequestHandler() {
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
        }
    );

    protected HttpRequestHandler handler(String host, int port) {
        return this.handler.get();
    }

    @After
    public void tearDown() throws Exception {
        this.server.shutdown();
        this.server.awaitTermination();
    }


    protected void assertNominalResponse(Response response) throws IOException {
        assertThat(response.code(), is(200));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Test response : OK."));
    }

    protected void asserHeaderTooLargeResponse(Response response) throws IOException {
        assertThat(response.code(), is(431));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("Request Header Fields Too Large"));
    }

    protected void assertURITooLongResponse(Response response) throws IOException {
        assertThat(response.code(), is(414));
        assertThat(response.header("content-type"), is("text/plain; charset=UTF-8"));
        assertThat(response.body().string(), is("URI Too Long"));
    }


    @Test
    public void whenNominalGet__thenOK() throws Exception {
        Call call = this.client.newCall(new Request.Builder()
                .url(this.url + "/path/to/here")
                        .get()
                .build());
        Response response = call.execute();

        this.assertNominalResponse(response);
    }

    @Test
    public void whenNominalPost__thenOK() throws Exception {
        Call call = this.client.newCall(new Request.Builder()
                .url(this.url + "/path/to/here")
                        .post(RequestBody.create("zoubidou", MediaType.get("text/plain")))
                .build());
        Response response = call.execute();

        this.assertNominalResponse(response);
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

        this.assertNominalResponse(response);

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

        this.assertNominalResponse(response);

    }

    @Test
    public void whenOver16kHeaders__then431() throws Exception {
        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here");
        for (int i = 0; i * LINE.length() < 30000 ; i++) {
            request.header("header-" + i, LINE);
        }
        Response response = this.client.newCall(request.build()).execute();

        this.asserHeaderTooLargeResponse(response);
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

        this.assertNominalResponse(response);
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

        this.assertNominalResponse(response);
    }

    @Test
    public void whenURIOver8k__then414() throws Exception {
        String url = this.url;
        for (int i = 0; i * LINE.length() < 13000 ; i++) {
            url += "/" + LINE;
        }
        Request.Builder request = new Request.Builder()
                .url(url).get();
        Response response = this.client.newCall(request.build()).execute();

        this.assertURITooLongResponse(response);
    }

    @Test
    public void whenServing25MoFile__thenOK() throws Exception {
        byte[] content = this.generateInRam(25).getBytes(StandardCharsets.UTF_8);

        this.handler.set(new HttpRequestHandler() {
            @Override
            protected HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body) {
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                response.setStatus(OK);

                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.length);
                response.content().writeBytes(content);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                return response;
            }
        });

        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here").get();
        Response response = this.client.newCall(request.build()).execute();

        assertThat(response.body().contentLength(), is(Long.valueOf(content.length)));
        assertThat(response.code(), is(200));
    }

    @Test
    public void whenReading100MoUpload__thenOK() throws Exception {
        File upload = this.generateFile(100);
        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here").post(RequestBody.create(upload, MediaType.parse("text/plain")));
        Response response = this.client.newCall(request.build()).execute();
        this.assertNominalResponse(response);
    }

    @Test
    public void whenReadingMoreThan100MoUpload__thenKO() throws Exception {
        File upload = this.generateFile(110);
        Request.Builder request = new Request.Builder()
                .url(this.url + "/path/to/here").post(RequestBody.create(upload, MediaType.parse("text/plain")));
        Response response = this.client.newCall(request.build()).execute();
        assertThat(response.code(), is(413));
    }

    private File generateFile(int megs) throws IOException {
        File upload = File.createTempFile("upload", ".bin", this.dir.getRoot());
        int total = 0;
        try(OutputStream out = new FileOutputStream(upload)) {
            byte[] buffer = LINE.getBytes(StandardCharsets.UTF_8);
            while(total < megs * 1024 * 1024) {
                out.write(buffer);
                total += buffer.length;
            }
            out.flush();
        }
        return upload;
    }

    private String generateInRam(int megs) throws IOException {
        int total = 0;
        try(OutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = LINE.getBytes(StandardCharsets.UTF_8);
            while(total < megs * 1024 * 1024) {
                out.write(buffer);
                total += buffer.length;
            }
            out.flush();
            out.close();
            return out.toString();
        }
    }
}