package org.codingmatters.rest.api.generator.client;

import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.codingmatters.tests.compile.helpers.ClassLoaderHelper;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.API_PACK;
import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.CLIENT_PACK;

public class ClientInterfaceGeneratorRequestResponseTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;
    private ClassLoaderHelper classes;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-request-response.raml"));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        Spec anAlreadyDefinedValueObject = new Spec.Builder()
                .addValue(ValueSpec.valueSpec()
                        .name("AnAlreadyDefinedValueObject")
                )
                .build();
        new SpecCodeGenerator(anAlreadyDefinedValueObject,
                "org.codingmatters", this.dir.getRoot()).generate();
        new JsonFrameworkGenerator(anAlreadyDefinedValueObject, "org.codingmatters", this.dir.getRoot()).generate();


        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = ClientGeneratorHelper.compile(this.dir.getRoot());
        this.classes = this.compiled.classLoader();

        this.fileHelper.printJavaContent("", this.dir.getRoot());
    }

    @Test
    public void compilesFine() {
    }
}
