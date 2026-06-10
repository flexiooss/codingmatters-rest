package org.codingmatters.rest.server.acceptance;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public abstract class SseAcceptanceTest extends BaseAcceptanceTest {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    @Test
    public void givenOpenSse__thenContentTypeIsSse() throws Exception {
        this.withProcessor((requestDelegate, responseDelegate) -> {
            responseDelegate.openSse().close();
        });

        Request request = this.requestBuilder()
                .addHeader("Accept", "text/event-stream")
                .get()
                .build();
        Response response = this.client.newCall(request).execute();
        assertThat(response.header("Content-Type"), containsString("text/event-stream"));
        response.close();
    }

    @Test
    public void givenOpenSse__whenSendEvent__thenClientReceivesFormattedSse() throws Exception {
        this.withProcessor((requestDelegate, responseDelegate) -> {
            try (var channel = responseDelegate.openSse()) {
                channel.send("message", "hello");
            }
        });

        Request request = this.requestBuilder()
                .addHeader("Accept", "text/event-stream")
                .get()
                .build();
        Response response = this.client.newCall(request).execute();
        String body = response.body().string();
        assertThat(body, containsString("event: message\ndata: hello\n\n"));
    }

    @Test
    public void givenOpenSse__whenSendMultipleEvents__thenClientReceivesAll() throws Exception {
        this.withProcessor((requestDelegate, responseDelegate) -> {
            try (var channel = responseDelegate.openSse()) {
                channel.send("1");
                channel.send("2");
            }
        });

        Request request = this.requestBuilder()
                .addHeader("Accept", "text/event-stream")
                .get()
                .build();
        Response response = this.client.newCall(request).execute();
        String body = response.body().string();
        assertThat(body, containsString("data: 1"));
        assertThat(body, containsString("data: 2"));
    }
}
