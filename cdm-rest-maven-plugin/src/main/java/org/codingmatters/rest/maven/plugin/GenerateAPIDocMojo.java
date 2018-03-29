package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.api.doc.ApiHtmlDocGenerator;
import org.codingmatters.rest.api.doc.ApiPumlGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.PumlClassFromSpecGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

import static org.codingmatters.rest.api.doc.DocHelper.camelCased;

@Mojo(name = "generate-api-doc")
public class GenerateAPIDocMojo extends AbstractGenerateAPIMojo {

    @Parameter(defaultValue = "${basedir}/target/api-doc/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RamlModelResult ramlModel = this.resolveRamlModel();
        try {
            new ApiPumlGenerator(ramlModel, this.outputDirectory).generate();
            Spec spec = new ApiTypesGenerator().generate(ramlModel);
            new PumlClassFromSpecGenerator(spec, camelCased(ramlModel.getApiV10().title().value()), this.outputDirectory).generate();

            new ApiHtmlDocGenerator(ramlModel, this.outputDirectory).generate();
        } catch (IOException | RamlSpecException e) {
            throw new MojoExecutionException("error generating doc from raml api", e);
        }
    }
}
