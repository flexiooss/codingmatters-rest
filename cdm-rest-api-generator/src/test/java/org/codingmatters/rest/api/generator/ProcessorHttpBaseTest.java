package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.api.tests.utils.FileHelper;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.codingmatters.tests.compile.CompiledCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/25/17.
 */
public class ProcessorHttpBaseTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    private OkHttpClient client = new OkHttpClient();

    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));

    private Processor testProcessor;

    private void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        this.testProcessor.process(requestDelegate, responseDelegate);
    }

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-base.raml");
        this.compiled = helper.compiled();
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
        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));
        assertThat(response.body().contentType().charset().displayName(), is("UTF-8"));
        assertThat(response.body().contentType(), is(MediaType.parse("application/json; charset=utf-8")));
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

    private void setupProcessorWithHandler(String handlerMethod, Function handler) throws Exception {
        Object builder = this.compiled.getClass("org.generated.server.TestAPIHandlers$Builder").newInstance();
        builder = this.compiled.on(builder).invoke(handlerMethod, Function.class).with(handler);
        Object handlers = this.compiled.on(builder).invoke("build");

        this.testProcessor = (Processor) this.compiled.getClass("org.generated.server.TestAPIProcessor")
                .getConstructor(
                        String.class,
                        JsonFactory.class,
                        this.compiled.getClass("org.generated.server.TestAPIHandlers"))
                .newInstance("/api", new JsonFactory(), handlers);
    }
}
