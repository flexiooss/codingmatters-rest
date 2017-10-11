package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.api.generator.ClientHandlerImplementation;
import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.ClientRequesterImplementation;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;


@Mojo(name = "generate-client-side")
public class GenerateAPIClientSideMojo extends AbstractGenerateAPIMojo {

    @Parameter(required = true, alias = "destination-package")
    private String destinationPackage;

    @Parameter(required = true, alias = "types-package")
    private String typesPackage;

    @Parameter(required = true, alias = "api-package")
    private String apiPackage;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RamlModelResult raml = this.resolveRamlModel();

        try {
            new ClientInterfaceGenerator(this.destinationPackage, this.apiPackage, this.outputDirectory).generate(raml);
        } catch (IOException e) {
            throw new MojoExecutionException("error generating client interface from raml model", e);
        }
        try {
            new ClientRequesterImplementation(this.destinationPackage, this.apiPackage, this.typesPackage, this.outputDirectory).generate(raml);
        } catch (IOException e) {
            throw new MojoExecutionException("error generating requester client implementation from raml model", e);
        }
        try {
            new ClientHandlerImplementation(this.destinationPackage, this.apiPackage, this.typesPackage, this.outputDirectory).generate(raml);
        } catch (IOException e) {
            throw new MojoExecutionException("error generating handler client implementation from raml model", e);
        }

    }
}
