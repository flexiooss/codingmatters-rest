package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.okhttp.exception.ConnectionTimeoutException;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public class OkHttpClientWrapper {

    static public final String OK_WRAPPER_CONNECT_TIMEOUT_ENV = "OK_WRAPPER_CONNECT_TIMEOUT";
    static public final String OK_WRAPPER_READ_TIMEOUT_ENV = "OK_WRAPPER_READ_TIMEOUT";
    static public final String OK_WRAPPER_WRITE_TIMEOUT_ENV = "OK_WRAPPER_WRITE_TIMEOUT";

    static public OkHttpClientWrapper build() {
        return build(new OkHttpClient.Builder()
                .connectTimeout(envLong(OK_WRAPPER_CONNECT_TIMEOUT_ENV, "2000"), TimeUnit.MILLISECONDS)
                .readTimeout(envLong(OK_WRAPPER_READ_TIMEOUT_ENV, "40000"), TimeUnit.MILLISECONDS)
                .writeTimeout(envLong(OK_WRAPPER_WRITE_TIMEOUT_ENV, "40000"), TimeUnit.MILLISECONDS)
        );
    }

    /**
     * This one should only used in unit tests.
     *
     * Please consider using the no arg build() anv set timeout values with env / system property variables.
     *
     * @param builder
     * @return
     */
    @Deprecated
    static public OkHttpClientWrapper build(OkHttpClient.Builder builder) {
        return from(builder.build());
    }

    /**
     * This one should only used in unit tests.
     *
     * Please consider using the no arg build() anv set timeout values with env / system property variables.
     *
     * @param client
     * @return
     */
    @Deprecated
    static public OkHttpClientWrapper from(OkHttpClient client) {
        return new OkHttpClientWrapper(client);
    }

    static private Long envLong(String name, String defaultValue) {
        return Long.parseLong(envString(name, defaultValue));
    }
    static private String envString(String name, String defaultValue) {
        if(System.getenv(name) != null) {
            return System.getenv(name);
        }
        return System.getProperty(name.replaceAll("_", ".").toLowerCase(), defaultValue);
    }



    private final OkHttpClient delegate;

    private OkHttpClientWrapper(OkHttpClient delegate) {
        this.delegate = delegate;
    }

    public Response execute(Request request) throws IOException {
        try {
            return this.delegate.newCall(request).execute();
        } catch (SocketTimeoutException e) {
            if(e.getMessage().equals("connect timed out")) {
                throw new ConnectionTimeoutException("connection timed out", e);
            } else {
                throw e;
            }
        } catch (NoRouteToHostException e) {
            throw new ConnectionTimeoutException("connection timed out", e);
        }
    }
}
