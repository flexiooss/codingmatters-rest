package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/25/17.
 */
public class ProcessorHttpBaseTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-base.raml");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void rootGet() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "rootGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/").build()).execute();

        assertThat(response.code(), is(200));
        /*
        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));
        assertThat(response.body().contentType().charset().displayName(), is("UTF-8"));
        assertThat(response.body().contentType(), is(MediaType.parse("application/json; charset=utf-8")));
        */
        assertThat(hit.get(), is(1L));
    }

    @Test
    public void rootDelete() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "rootDeleteHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/").delete().build()).execute();

        assertThat(hit.get(), is(1L));
    }

    @Test
    public void firstChild() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "child1GetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/child1").get().build()).execute();

        assertThat(hit.get(), is(1L));
    }

    @Test
    public void secondChild() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "child2GetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/child2").get().build()).execute();

        assertThat(hit.get(), is(1L));
    }

    @Test
    public void subchildChild() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "subchildGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/child1/subchild").get().build()).execute();

        assertThat(hit.get(), is(1L));
    }

}
