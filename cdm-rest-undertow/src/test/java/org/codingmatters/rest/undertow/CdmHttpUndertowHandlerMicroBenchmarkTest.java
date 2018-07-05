package org.codingmatters.rest.undertow;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.management.ManagementFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class CdmHttpUndertowHandlerMicroBenchmarkTest extends AbstractUndertowTest {

    private OkHttpClient client = new OkHttpClient();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void offHeapOnBinaryResponse() throws Exception {
        this.withProcessor((req, resp) -> {
            byte [] bytes = this.bytes(8192);
            resp.contenType("application/octet-stream").payload(bytes);
        });

        for (int i = 0; i < 100000; i++) {
            Response response = this.client.newCall(this.requestBuilder().get().build()).execute();
            assertThat(response.code(), is(200));
            response.close();
            if(i % 100 == 0) {
                System.out.printf(
                        "%08d - non heap used : %s - heap used : %s\n",
                        i,
                        ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed(),
                        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()
                );
            }
        }
    }

    @Test
    public void offHeapOnTextResponse() throws Exception {
        this.withProcessor((req, resp) -> {
            resp.contenType("text/plain").payload("yopyop tagada tralala y too plop plop plop", "utf-8");
        });

        for (int i = 0; i < 100000; i++) {
            this.client.newCall(this.requestBuilder().get().build()).execute().close();
            if(i % 100 == 0) {
                System.out.println(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().toString());
            }
        }
    }

    @Test
    public void offHeapOnEmptyResponse() throws Exception {
        this.withProcessor((req, resp) -> {
            resp.contenType("application/octet-stream");
        });

        for (int i = 0; i < 1000000; i++) {
            this.client.newCall(this.requestBuilder().get().build()).execute().close();
            if(i % 100 == 0) {
                System.out.println(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().toString());
            }
        }
    }

    private byte[] bytes(int size) {
        byte[] result = new byte[size];
        return result;
    }

}