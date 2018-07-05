package org.codingmatters.rest.undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;

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

            exchange.startBlocking();
            //exchange.getResponseSender().send(ByteBuffer.wrap(new byte[4096]));
            try {
                try(OutputStream out = exchange.getOutputStream()) {
                    out.write(new byte[4096]);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            exchange.endExchange();
            return;
        }
    });

    private OkHttpClient client = new OkHttpClient();

    @Test
    public void name() throws Exception {
        for (int i = 0; i < 100000; i++) {
            Response response = this.client.newCall(new Request.Builder()
                    .url(this.undertow.baseUrl())
                    .get().build())
                    .execute();
            assertThat(response.code(), is(200));
            if(i % 100 == 0) {
                System.out.printf(
                        "%010d - non heap used : %010d - heap used : %010d\n",
                        i,
                        ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed(),
                        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()
                );
            }
        }
    }
}
