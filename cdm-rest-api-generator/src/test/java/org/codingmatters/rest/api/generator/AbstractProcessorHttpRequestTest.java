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
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.tests.compile.helpers.helpers.ObjectHelper;
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
    protected ClassLoaderHelper classes;
    protected OkHttpClient client = new OkHttpClient();

    private void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        this.testProcessor.process(requestDelegate, responseDelegate);
    }

    protected void setupProcessorWithHandler(String handlerMethod, Function handler) throws Exception {
        ObjectHelper handlers = this.classes.get("org.generated.api.TestAPIHandlers$Builder")
                .newInstance()
                .call(handlerMethod, Function.class).with(handler)
                .call("build");

        this.testProcessor = (Processor) this.classes.get("org.generated.server.TestAPIProcessor")
                .newInstance(String.class, JsonFactory.class, this.classes.get("org.generated.api.TestAPIHandlers").get())
                .with("/api", new JsonFactory(), handlers.get())
                .get();
    }

}
