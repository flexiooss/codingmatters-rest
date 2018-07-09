package org.codingmatters.rest.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class UndertowMicroBenchmarkTest {

    @Rule
    public UndertowResource undertow = new UndertowResource(new HttpHandler() {
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {

            if(exchange.isInIoThread()) {
                exchange.dispatch(this);
                return;
            }


            if(! "false".equals(System.getProperty("do.send"))) {
                //exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                //exchange.getResponseSender().send("Hello World");
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
                exchange.getResponseSender().send(ByteBuffer.wrap(new byte[4096]));
            }
//            exchange.startBlocking();
//            exchange.getResponseSender().send(ByteBuffer.wrap(new byte[4096]));
//            try {
//                try(OutputStream out = exchange.getOutputStream()) {
//                    out.write(new byte[4096]);
//                    out.flush();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            exchange.endExchange();
            return;
        }
    });

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(5, 2, TimeUnit.SECONDS))
            .build();

    @Test
    public void name() throws Exception {
        System.out.println(this.undertow.server().getWorker().getIoThreadCount() + " io threads.");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000 ; i++) {
            Response response = this.client.newCall(new Request.Builder()
                    .url(this.undertow.baseUrl())
                    .get().build())
                    .execute();
            response.close();
            assertThat(response.code(), is(200));
            if(i % 100 == 0) {
                long ellapsed = System.currentTimeMillis() - start;
                System.out.printf(
                        "%010d - %04d- non heap used : %010d - heap used : %010d\n",
                        i,
                        ellapsed,
                        ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed(),
                        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()
                );
                start = System.currentTimeMillis();
            }
        }
    }
}
