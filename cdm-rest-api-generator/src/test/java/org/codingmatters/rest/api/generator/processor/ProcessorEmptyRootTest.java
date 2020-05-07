package org.codingmatters.rest.api.generator.processor;

import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProcessorEmptyRootTest extends AbstractProcessorHttpRequestTest {

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
//                .printFileTree(true)
                .setUpWithResource("processor/processor-empty-root.raml");
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");
        this.compiled = helper.compiled();
        this.classes = this.compiled.classLoader();
    }

    @Test
    public void givenGettingRoot__whenGettingWithTrailingSlash__then200_andHandlerHit() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "rootGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/").build()).execute();

        assertThat(response.code(), is(200));
        assertThat(hit.get(), is(1L));
    }

    @Test
    public void givenGettingRoot__whenGettingWithoutTrailingSlash__then200_andHandlerHit() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "rootGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api").build()).execute();

        assertThat(response.code(), is(200));
        assertThat(hit.get(), is(1L));
    }

    @Test
    public void givenGettingChild__whenGettingWithTrailingSlash__then200_andHandlerHit() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "childGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/child/").get().build()).execute();

        assertThat(response.code(), is(200));
        assertThat(hit.get(), is(1L));
    }

    @Test
    public void givenGettingChild__whenGettingWithoutTrailingSlash__then200_andHandlerHit() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "childGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/child").get().build()).execute();

        assertThat(response.code(), is(200));
        assertThat(hit.get(), is(1L));
    }

    @Test
    public void whenGettingChildWithUriParam__then200_andHandlerHit() throws Exception {
        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "childWithUriParamGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return null;
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/value").get().build()).execute();

        assertThat(response.code(), is(200));
        assertThat(hit.get(), is(1L));
    }
}
