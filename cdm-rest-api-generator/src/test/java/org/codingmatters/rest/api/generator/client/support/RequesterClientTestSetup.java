package org.codingmatters.rest.api.generator.client.support;

import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.ClientRequesterImplementation;
import org.codingmatters.tests.compile.CompiledCode;
import org.codingmatters.tests.compile.FileHelper;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import static org.codingmatters.rest.api.generator.client.support.ClientGeneratorHelper.*;

public class RequesterClientTestSetup extends ExternalResource {

    private final String ramlResource;
    private final TemporaryFolder dir;
    private final FileHelper fileHelper;
    private CompiledCode compiled;

    public RequesterClientTestSetup(String ramlResource, TemporaryFolder dir, FileHelper fileHelper) {
        this.ramlResource = ramlResource;
        this.dir = dir;
        this.fileHelper = fileHelper;
    }

    @Override
    protected void before() throws Throwable {
        RamlModelResult raml = new RamlModelBuilder().buildApi(this.fileHelper.fileResource(this.ramlResource));
        ClientGeneratorHelper.generateBase(raml, this.dir.getRoot());

        new ClientInterfaceGenerator(CLIENT_PACK, API_PACK, this.dir.getRoot()).generate(raml);
        new ClientRequesterImplementation(CLIENT_PACK, API_PACK, TYPES_PACK, this.dir.getRoot()).generate(raml);

        this.compiled = ClientGeneratorHelper.compile(this.dir.getRoot());
    }

    @Override
    protected void after() {}


    public CompiledCode compiled() {
        return compiled;
    }
}
