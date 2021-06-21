package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.okhttp.exception.ConnectionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

public class OkHttpClientWrapper implements HttpClientWrapper {

    static private final Logger log = LoggerFactory.getLogger(OkHttpClientWrapper.class);

    static public final String OK_WRAPPER_CONNECT_TIMEOUT_ENV = "OK_WRAPPER_CONNECT_TIMEOUT";
    static public final String OK_WRAPPER_READ_TIMEOUT_ENV = "OK_WRAPPER_READ_TIMEOUT";
    static public final String OK_WRAPPER_WRITE_TIMEOUT_ENV = "OK_WRAPPER_WRITE_TIMEOUT";

    static public HttpClientWrapper build() {
        return build(builder -> builder);
    }

    static public HttpClientWrapper build(Config config) {
        OkHttpClient.Builder builder = defaultConfiguration(new OkHttpClient.Builder());
        try {
            return new OkHttpClientWrapper(config.apply(builder).build());
        } catch (Exception e) {
            throw new RuntimeException("error building http client from config", e);
        }
    }

    private static OkHttpClient.Builder defaultConfiguration(OkHttpClient.Builder builder) {
        return builder
                .connectTimeout(envLong(OK_WRAPPER_CONNECT_TIMEOUT_ENV, "2000"), TimeUnit.MILLISECONDS)
                .readTimeout(envLong(OK_WRAPPER_READ_TIMEOUT_ENV, "40000"), TimeUnit.MILLISECONDS)
                .writeTimeout(envLong(OK_WRAPPER_WRITE_TIMEOUT_ENV, "40000"), TimeUnit.MILLISECONDS);
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

    @Override
    public Response execute(Request request) throws IOException {
        try {
            return this.delegate.newCall(request).execute();
        } catch (SocketTimeoutException e) {
            if(e.getMessage() != null && e.getMessage().equalsIgnoreCase("connect timed out")) {
                throw new ConnectionTimeoutException("connection timed out", e);
            } else {
                throw e;
            }
        } catch (NoRouteToHostException e) {
            throw new ConnectionTimeoutException("connection timed out", e);
        }
    }

    @FunctionalInterface
    public interface Config {
        OkHttpClient.Builder apply(OkHttpClient.Builder builder) throws Exception;

        static Config truststoreFrompath(String path) {
            return builder -> {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
                SSLContext context = SSLContext.getInstance("TLS");
                KeyStore keyStore = KeyStore.getInstance("JKS");
                try (InputStream is = new FileInputStream(path)) {
                    keyStore.load(is, null);
                }
                trustManagerFactory.init(keyStore);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

                context.init(null, trustManagers, null);

                return builder.sslSocketFactory(context.getSocketFactory(), (X509TrustManager) trustManagers[0]);
            };
        }

    }

}
