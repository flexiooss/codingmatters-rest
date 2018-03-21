package org.codingmatters.rest.undertow;

import okhttp3.OkHttpClient;
import org.codingmatters.rest.api.RequestDelegate;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/7/17.
 */
public class UndertowRequestDelegateTest extends AbstractUndertowTest {

    private OkHttpClient client = new OkHttpClient();

    @Test
    public void method_get() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().get().build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.GET));
    }

    @Test
    public void method_post() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().post(this.emptyJsonBody()).build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.POST));
    }

    @Test
    public void method_put() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().put(this.emptyJsonBody()).build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.PUT));
    }

    @Test
    public void method_patch() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().patch(this.emptyJsonBody()).build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.PATCH));
    }

    @Test
    public void method_delete() throws Exception {
        AtomicReference<RequestDelegate.Method> method = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {method.set(requestDeleguate.method());});

        this.client.newCall(this.requestBuilder().delete().build()).execute();
        assertThat(method.get(), is(RequestDelegate.Method.DELETE));
    }

    @Test
    public void pathMatcher() throws Exception {
        AtomicReference<Matcher> matcher = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {matcher.set(requestDeleguate.pathMatcher("/[a]+/[b]+"));});

        this.client.newCall(this.requestBuilder("/aaa/bbb").get().build()).execute();
        assertThat(matcher.get().matches(), is(true));
    }

    @Test
    public void absolutePath() throws Exception {
        AtomicReference<String> absolutePath = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {absolutePath.set(requestDeleguate.absolutePath("aaa/bbb"));});

        this.client.newCall(this.requestBuilder().get().build()).execute();
        assertThat(absolutePath.get(), is(this.baseUrl() + "/aaa/bbb"));
    }

    @Test
    public void payload() throws Exception {
        AtomicReference<String> payload = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            String result = this.readAsString(requestDeleguate);
            payload.set(result);
        });

        String requestPayload = "{\"yop\":\"yop\"}";
        this.client.newCall(this.requestBuilder().post(this.jsonBody(requestPayload)).build()).execute();
        assertThat(payload.get(), is(requestPayload));
    }

    @Test
    public void queryParameters_singleParams() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n1=v1&n2=v2").get().build()).execute();
        assertThat(queryParameters.get().size(), is(2));
        assertThat(queryParameters.get().get("n1"), contains("v1"));
        assertThat(queryParameters.get().get("n2"), contains("v2"));
    }

    @Test
    public void queryParameters_multipleParams() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n=v1&n=v2").get().build()).execute();
        assertThat(queryParameters.get().size(), is(1));
        assertThat(queryParameters.get().get("n"), contains("v1", "v2"));
    }

    @Test
    public void queryParameters_undefined() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n=v").get().build()).execute();
        assertThat(queryParameters.get().size(), is(1));
        assertThat(queryParameters.get().get("undefined"), is(nullValue()));
    }

    @Test
    public void queryParameters_emptyParam() throws Exception {
        AtomicReference<Map<String, List<String>>> queryParameters = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {queryParameters.set(requestDeleguate.queryParameters());});

        this.client.newCall(this.requestBuilder("?n=").get().build()).execute();
        assertThat(queryParameters.get().size(), is(1));
        assertThat(queryParameters.get().get("n"), contains(""));
    }

    @Test
    public void path() throws Exception {
        AtomicReference<RequestDelegate> req = new AtomicReference<>();
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            req.set(requestDeleguate);
        });

        this.client.newCall(this.requestBuilder("/request/path").get().build()).execute();
        assertThat(req.get().path(), is("/request/path"));

        this.client.newCall(this.requestBuilder("/request/path?yop=yop").get().build()).execute();
        assertThat(req.get().path(), is("/request/path"));

        this.client.newCall(this.requestBuilder("/request/path/").get().build()).execute();
        assertThat(req.get().path(), is("/request/path/"));

        this.client.newCall(this.requestBuilder("").get().build()).execute();
        assertThat(req.get().path(), is("/"));
    }
}