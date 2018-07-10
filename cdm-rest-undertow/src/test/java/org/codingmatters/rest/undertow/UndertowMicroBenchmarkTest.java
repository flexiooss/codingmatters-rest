package org.codingmatters.rest.undertow;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class UndertowMicroBenchmarkTest {

    static private String date = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date());

    private final HttpHandler handler = new HttpHandler() {
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {

            if (exchange.isInIoThread()) {
                exchange.dispatch(this);
                return;
            }


            if (!"false".equals(System.getProperty("do.send"))) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
                exchange.getResponseSender().send(ByteBuffer.wrap(new byte[4096]));
            }
            exchange.endExchange();
            return;
        }
    };

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(5, 2, TimeUnit.SECONDS))
            .build();
    private String baseUrl;

    @Test
    public void defaults() throws Exception {
        this.runBenchmark(Undertow.builder(), "defaults");
    }

    @Test
    public void defaultsWithMaxCachedBufferSizePropToZero() throws Exception {
        String prev = System.getProperty("jdk.nio.maxCachedBufferSize");
        System.setProperty("jdk.nio.maxCachedBufferSize", "0");
        try {
            this.runBenchmark(Undertow.builder(), "defaults with jdk.nio.maxCachedBufferSize=0");
        } finally {
            if(prev != null) {
                System.setProperty("jdk.nio.maxCachedBufferSize", prev);
            } else {
                System.clearProperty("jdk.nio.maxCachedBufferSize");
            }
        }
    }

    @Test
    public void directBufferToFalse() throws Exception {
        this.runBenchmark(Undertow.builder().setDirectBuffers(false), "no direct buffers");
    }

    @Test
    public void directBufferToFalseWithBufferSizeTo16K() throws Exception {
        this.runBenchmark(Undertow.builder().setDirectBuffers(false).setBufferSize(1024 * 16 - 20), "no direct buffers and 16K buffers");
    }


    @Test
    public void directBufferToFalseWithMaxCachedBufferSizePropToZero() throws Exception {
        String prev = System.getProperty("jdk.nio.maxCachedBufferSize");
        System.setProperty("jdk.nio.maxCachedBufferSize", "0");
        try {
            this.runBenchmark(Undertow.builder().setDirectBuffers(false), "no direct buffers with jdk.nio.maxCachedBufferSize=0");
        } finally {
            if(prev != null) {
                System.setProperty("jdk.nio.maxCachedBufferSize", prev);
            } else {
                System.clearProperty("jdk.nio.maxCachedBufferSize");
            }
        }
    }


    @Test
    public void directBufferToFalseWithMaxCachedBufferSizePropToZeroAndBufferSizeTo16K() throws Exception {
        String prev = System.getProperty("jdk.nio.maxCachedBufferSize");
        System.setProperty("jdk.nio.maxCachedBufferSize", "0");
        try {
            this.runBenchmark(Undertow.builder().setDirectBuffers(false).setBufferSize(1024 * 16 - 20), "no direct buffers with jdk.nio.maxCachedBufferSize=0 and 16K buffers");
        } finally {
            if(prev != null) {
                System.setProperty("jdk.nio.maxCachedBufferSize", prev);
            } else {
                System.clearProperty("jdk.nio.maxCachedBufferSize");
            }
        }
    }

    private Undertow start(Undertow.Builder undertowBuilder) throws IOException {
        int port;
        try(ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        Undertow server = undertowBuilder
                .addHttpListener(port, "localhost")
                .setHandler(this.handler)
                .build();
        server.start();

        this.baseUrl = "http://localhost:" + port;

        return server;
    }

    private void runBenchmark(Undertow.Builder undertowBuilder, String description) throws Exception {
        Undertow undertow = this.start(undertowBuilder);
        Report heapReport = new Report(   "heap used    ");
        Report nonHeapReport = new Report("non heap used");


        try {
            long start = System.currentTimeMillis();
            boolean warmup = true;
            for (int i = 0; i < 1_200_000; i++) {
                if(i == 200_000) {
                    warmup = false;
                }
                Response response = client.newCall(new Request.Builder()
                        .url(this.baseUrl)
                        .get().build())
                        .execute();
                response.close();
                assertThat(response.code(), is(200));

                if(! warmup) {
                    heapReport.value(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
                    nonHeapReport.value(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed());
                    if(i % 10_000 == 0) {
                        System.gc();
                        Thread.sleep(1000);
                    }
                }
            }
            System.out.println(description);
            System.out.println(heapReport);
            System.out.println(nonHeapReport);

            try(Writer out = new FileWriter("/tmp/" + this.getClass().getSimpleName() + "-report.txt", true)) {
                out.write("\n");
                out.write(description + " [" + date + "]\n");
                out.write(heapReport.toString() + "\n");
                out.write(nonHeapReport.toString() + "\n");
                out.flush();
            }
        } finally {
            undertow.stop();
        }
    }

    private class Report {
        private final String name;
        private Long min = null;
        private Long max = null;
        private Long curent = null;

        private Report(String name) {
            this.name = name;
        }

        public void value(long v) {
            this.curent = v;
            if(this.min == null || v < this.min) {
                this.min = v;
            }

            if(this.max == null || v > this.max) {
                this.max = v;
            }
        }

        @Override
        public String toString() {
            return String.format("%s - min=%010d ; max=%010d ; current=%010d", this.name, this.min, this.max,this.curent);
        }
    }
}
