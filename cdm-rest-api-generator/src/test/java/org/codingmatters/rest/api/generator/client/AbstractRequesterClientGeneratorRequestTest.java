package org.codingmatters.rest.api.generator.client;

import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.ClientRequesterImplementation;
import org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;

public abstract class AbstractRequesterClientGeneratorRequestTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Rule
    public FileHelper fileHelper = new FileHelper();

    private CompiledCode compiled;

    @Before
    public void setUp() throws Exception {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource("processor/processor-request.raml"));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);
        new ClientRequesterImplementation(CLIENT_PACK, API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

        this.fileHelper.printJavaContent("", this.dir.getRoot());
        this.fileHelper.printFile(this.dir.getRoot(), "TestAPIRequesterClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "PayloadClient.java");
        this.fileHelper.printFile(this.dir.getRoot(), "PayloadPostRequest.java");

        this.compiled = ClientGeneratorHelper.compile(this.dir.getRoot());
    }

    public CompiledCode compiled() {
        return compiled;
    }
}
