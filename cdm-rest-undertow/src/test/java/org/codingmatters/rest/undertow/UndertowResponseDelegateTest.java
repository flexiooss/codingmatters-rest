package org.codingmatters.rest.undertow;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/7/17.
 */
public class UndertowResponseDelegateTest extends AbstractUndertowTest {

    private OkHttpClient client = new OkHttpClient();

    @Test
    public void contenType() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.contenType("yip/yop");});

        MediaType contentType = this.client.newCall(this.requestBuilder().get().build()).execute().body().contentType();
        assertThat(contentType.type(), is("yip"));
        assertThat(contentType.subtype(), is("yop"));
    }

    @Test
    public void status() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.status(201);});

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().code(), is(201));
    }

    @Test
    public void addHeader() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.addHeader("yip", "yop");});

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().header("yip"), is("yop"));
    }

    @Test
    public void payload() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.payload("yop yop", "utf-8");});

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().body().string(), is("yop yop"));
    }
}
