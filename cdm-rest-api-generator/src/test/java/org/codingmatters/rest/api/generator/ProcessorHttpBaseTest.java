package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
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
    public void root() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIProcessor.java");

        AtomicLong hit = new AtomicLong(0);

        Function handler = o -> {
            hit.incrementAndGet();
            return null;
        };

        Object builder = this.compiled.getClass("org.generated.server.TestAPIHandlers$Builder").newInstance();
        builder = this.compiled.on(builder).invoke("rootGetHandler", Function.class).with(handler);
        Object handlers = this.compiled.on(builder).invoke("build");
        assertThat(
                this.compiled.on(handlers).castedTo("org.generated.server.TestAPIHandlers").invoke("rootGetHandler"),
                is(handler)
        );

        this.testProcessor = (Processor) this.compiled.getClass("org.generated.server.TestAPIProcessor")
                .getConstructor(
                        String.class,
                        JsonFactory.class,
                        this.compiled.getClass("org.generated.server.TestAPIHandlers"))
                .newInstance("/api", new JsonFactory(), handlers);

        Response response = this.client.newCall(new Request.Builder().url(this.undertow.baseUrl() + "/api/root/").build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));
        assertThat(response.body().contentType().charset().displayName(), is("UTF-8"));
        assertThat(hit.get(), is(1L));
    }
}
