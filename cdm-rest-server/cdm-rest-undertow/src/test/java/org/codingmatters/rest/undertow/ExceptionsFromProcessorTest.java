package org.codingmatters.rest.undertow;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class ExceptionsFromProcessorTest extends AbstractUndertowTest {
    @FunctionalInterface
    interface RaisingException {
        void raise() throws IOException;
    }

    @Parameterized.Parameters(name = "{0}")
    static public Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"IOException", (RaisingException) () -> {throw new IOException("test exception");}},
                {"RuntimeException", (RaisingException) () -> {throw new RuntimeException("test exception");}},
                {"AssertionError", (RaisingException) () -> {throw new AssertionError("test exception");}},
                {"nasty npe", (RaisingException) () -> {String nil = null; nil.substring(12);}}
        });
    }

    private final String message;
    private final RaisingException raisingException;

    public ExceptionsFromProcessorTest(String message, RaisingException raisingException) {
        this.message = message;
        this.raisingException = raisingException;
    }

    private OkHttpClient client = new OkHttpClient();

    @Test
    public void whenFirstRequestRaisesException__then500_andSecondRequestIsHandled() throws Exception {
        AtomicBoolean first = new AtomicBoolean(true);
        this.withProcessor((requestDelegate, responseDelegate) -> {
            if(first.getAndSet(false)) {
                raisingException.raise();
            } else {
                responseDelegate.status(201);
                responseDelegate.payload("up and running".getBytes());
            }
        });
        Response response = this.client.newCall(this.requestBuilder().get().build()).execute();

        assertThat(this.message + " - first response code", response.code(), is(500));

        response = this.client.newCall(this.requestBuilder().get().build()).execute();
        assertThat(this.message + " - second response code", response.code(), is(201));
        assertThat(this.message + " - second response body", response.body().string(), is("up and running"));
    }

}
