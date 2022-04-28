package org.codingmatters.rest.undertow;

import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Ignore
public class SaturatedUndertowTest extends AbstractUndertowTest {

    private OkHttpClient client = new OkHttpClient(new OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
    );

    ExecutorService clientPool = Executors.newFixedThreadPool(1000);

    @After
    public void tearDown() throws Exception {
        this.clientPool.shutdownNow();
    }

    @Test
    public void given__when__then() throws Exception {
        AtomicBoolean shouldWait = new AtomicBoolean(true);

        AtomicInteger serverRequestCount = new AtomicInteger(0);
        AtomicInteger serverCompletedRequestCount = new AtomicInteger(0);
        AtomicInteger clientRequestCount = new AtomicInteger(0);
        AtomicInteger clientCompletedRequestCount = new AtomicInteger(0);
        AtomicInteger clientTimeoutCount = new AtomicInteger(0);

        Set<String> timeoutMessages = Collections.synchronizedSet(new HashSet<>());


        this.withProcessor((requestDelegate, responseDelegate) -> {
            System.out.println("request from : " + requestDelegate.headers().get("test-client"));
            serverRequestCount.incrementAndGet();

            while(shouldWait.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("done for : " + requestDelegate.headers().get("test-client"));
            serverCompletedRequestCount.incrementAndGet();
        });

        for (int i = 0; i < 500; i++) {
            String client = String.format("client-%04d", i);
            this.clientPool.submit(() -> {
                try {
                    clientRequestCount.incrementAndGet();
                    this.client.newCall(this.requestBuilder().get().header("test-client", client).build()).execute();
                    System.out.println(client + " got a response");
                    clientCompletedRequestCount.incrementAndGet();

                } catch (IOException e) {
                    System.err.println(client + " ioe : " + e.getMessage());
                    timeoutMessages.add(messages(e));
                    clientTimeoutCount.incrementAndGet();
                }
            });
        }

        Thread.sleep(30 * 1000);

        shouldWait.set(false);

        Thread.sleep(30 * 1000);

        System.out.printf("server received  requests : %d\n", serverRequestCount.get());
        System.out.printf("server completed requests : %d\n", serverCompletedRequestCount.get());
        System.out.printf("client requests           : %d\n", clientRequestCount.get());
        System.out.printf("client completed requests : %d\n", clientCompletedRequestCount.get());
        System.out.printf("client timeouts           : %d\n", clientTimeoutCount.get());
        System.out.println("Timeout messages : ");
        for (String timeoutMessage : timeoutMessages) {
            System.out.printf("- %s\n", timeoutMessage);
        }

    }

    private String messages(Throwable e) {
        String result = e.getMessage();
        if(e.getCause() != null) {
            result += " | " + this.messages(e.getCause());
        }
        return result;
    }
}
