package org.codingmatters.rest.api.client.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.client.okhttp.exception.ConnectionTimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class OkHttpClientWrapper implements HttpClientWrapper {

    static public final String OK_WRAPPER_CONNECT_TIMEOUT_ENV = "OK_WRAPPER_CONNECT_TIMEOUT";
    static public final String OK_WRAPPER_READ_TIMEOUT_ENV = "OK_WRAPPER_READ_TIMEOUT";
    static public final String OK_WRAPPER_WRITE_TIMEOUT_ENV = "OK_WRAPPER_WRITE_TIMEOUT";

    static public HttpClientWrapper build() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return build(defaultConfiguration(builder)
        );
    }

    static public HttpClientWrapper buildWithTruststore(String path) {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
            SSLContext context = SSLContext.getInstance("TLS");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (InputStream is = new FileInputStream(path)) {
                keyStore.load(is, null);
            }
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            context.init(null, trustManagers, null);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(context.getSocketFactory(), (X509TrustManager) trustManagers[0]);
            return build(defaultConfiguration(builder)
            );
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | KeyManagementException e) {
            throw new RuntimeException("failed configuring http client with trusstore " + path, e);
        }
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
    static public HttpClientWrapper build(OkHttpClient.Builder builder) {
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
    static public HttpClientWrapper from(OkHttpClient client) {
        return new OkHttpClientWrapper(client);
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
