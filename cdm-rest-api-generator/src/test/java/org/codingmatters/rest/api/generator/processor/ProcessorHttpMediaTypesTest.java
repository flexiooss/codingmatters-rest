package org.codingmatters.rest.api.generator.processor;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.generator.AbstractProcessorHttpRequestTest;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ProcessorHttpMediaTypesTest extends AbstractProcessorHttpRequestTest {

    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-media-types.raml");
        this.compiled = helper.compiled();
        this.classes = helper.compiled().classLoader();
    }



    @Test
    public void rootGet() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "rootGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return classes.get("org.generated.api.RootGetResponse").call("builder")
                            .call("status200", classes.get("org.generated.api.rootgetresponse.Status200").get())
                                .with(classes.get("org.generated.api.rootgetresponse.Status200").call("builder").call("build").get())
                            .call("build").get();
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/").build()).execute();

        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));
        assertThat(response.body().contentType().charset().displayName(), is("UTF-8"));
        assertThat(response.body().contentType(), is(MediaType.parse("application/json; charset=utf-8")));
        assertThat(response.body().string(), is(""));

    }

    @Test
    public void unchangedGet() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "unchangedGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return classes.get("org.generated.api.UnchangedGetResponse").call("builder")
                            .call("status200", classes.get("org.generated.api.unchangedgetresponse.Status200").get())
                            .with(classes.get("org.generated.api.unchangedgetresponse.Status200").call("builder").call("build").get())
                            .call("build").get();
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/unchanged").build()).execute();

        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));
        assertThat(response.body().contentType().charset().displayName(), is("UTF-8"));
        assertThat(response.body().contentType(), is(MediaType.parse("application/json; charset=utf-8")));
        assertThat(response.body().string(), is(""));
    }


    @Test
    public void changedGet() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);
        this.setupProcessorWithHandler(
                "changedGetHandler",
                o -> {
                    hit.incrementAndGet();
                    return classes.get("org.generated.api.ChangedGetResponse").call("builder")
                            .call("status200", classes.get("org.generated.api.changedgetresponse.Status200").get())
                            .with(classes.get("org.generated.api.changedgetresponse.Status200").call("builder")
                                    .call("payload", String.class).with("body content")
                                    .call("build").get())
                            .call("build").get();
                });

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/changed").build()).execute();

        assertThat(response.body().contentType().type(), is("text"));
        assertThat(response.body().contentType().subtype(), is("html"));
        assertThat(response.body().contentType().charset().displayName(), is("UTF-8"));
        assertThat(response.body().contentType(), is(MediaType.parse("text/html; charset=utf-8")));
        assertThat(response.body().string(), is("body content"));
    }
}
