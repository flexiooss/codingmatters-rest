package org.codingmatters.rest.api.generator;

import com.fasterxml.jackson.core.JsonFactory;
import okhttp3.OkHttpClient;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.codingmatters.rest.undertow.support.UndertowResource;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.function.Function;

/**
 * Created by nelt on 5/26/17.
 */
public abstract class AbstractProcessorHttpRequestTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    @Rule
    public UndertowResource undertow = new UndertowResource(new CdmHttpUndertowHandler(this::process));

    protected Processor testProcessor;
    protected CompiledCode compiled;
    protected OkHttpClient client = new OkHttpClient();

    private void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        this.testProcessor.process(requestDelegate, responseDelegate);
    }

    protected void setupProcessorWithHandler(String handlerMethod, Function handler) throws Exception {
        Object builder = this.compiled.getClass("org.generated.api.TestAPIHandlers$Builder").newInstance();
        builder = this.compiled.on(builder).invoke(handlerMethod, Function.class).with(handler);
        Object handlers = this.compiled.on(builder).invoke("build");

        this.testProcessor = (Processor) this.compiled.getClass("org.generated.server.TestAPIProcessor")
                .getConstructor(
                        String.class,
                        JsonFactory.class,
                        this.compiled.getClass("org.generated.api.TestAPIHandlers"))
                .newInstance("/api", new JsonFactory(), handlers);
    }

}
