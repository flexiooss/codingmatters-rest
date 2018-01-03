package org.codingmatters.rest.api.generator.processor;

import com.fasterxml.jackson.core.JsonFactory;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.function.Function;

import static org.codingmatters.tests.reflect.ReflectMatchers.aPrivate;
import static org.codingmatters.tests.reflect.ReflectMatchers.aPublic;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/23/17.
 */
public class ProcessorGeneratorTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        ProcessorGeneratorTestHelper helper = new ProcessorGeneratorTestHelper(this.dir, this.fileHelper)
                .setUpWithResource("processor/processor-base.raml");


        this.compiled = helper.compiled();
    }

    @Test
    public void processorClass() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.server.TestAPIProcessor"),
                is(
                        aPublic().class_().implementing(Processor.class)
                                .with(aPublic().constructor()
                                        .withParameters(
                                                String.class,
                                                JsonFactory.class,
                                                this.compiled.getClass("org.generated.api.TestAPIHandlers")
                                        )
                                )
                )
        );
    }

    @Test
    public void privateFields() throws Exception {
        assertThat(
                this.compiled.getClass("org.generated.server.TestAPIProcessor"),
                is(
                        aPublic().class_().implementing(Processor.class)
                                .with(aPrivate().field().named("apiPath").withType(String.class))
                                .with(aPrivate().field().named("factory").withType(JsonFactory.class))
                                .with(aPrivate().field().named("handlers").withType(this.compiled.getClass("org.generated.api.TestAPIHandlers")))
                )
        );
    }

    @Test
    public void instantiate() throws Exception {
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIHandlers.java");

        Function rootGetHandler = o -> {
            try {
                Object builder = this.compiled.getClass("org.generated.api.RootGetResponse$Builder").newInstance();
                return this.compiled.on(builder).invoke("build");
            } catch (Exception e) {
                throw new AssertionError("error building response", e);
            }
        };

        assertThat(rootGetHandler.apply(null), is(notNullValue()));

        Object builder = this.compiled.getClass("org.generated.api.TestAPIHandlers$Builder").newInstance();
        builder = this.compiled.on(builder).invoke("rootGetHandler", Function.class).with(rootGetHandler);
        Object handlers = this.compiled.on(builder).invoke("build");
        assertThat(
                this.compiled.on(handlers).castedTo("org.generated.api.TestAPIHandlers").invoke("rootGetHandler"),
                is(rootGetHandler)
        );
    }
}