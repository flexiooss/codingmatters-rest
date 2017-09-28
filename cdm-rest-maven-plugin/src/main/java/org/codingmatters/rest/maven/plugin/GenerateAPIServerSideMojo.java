package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.api.generator.ProcessorGenerator;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

/**
 * Created by nelt on 5/17/17.
 */
@Mojo(name = "generate-server-side")
public class GenerateAPIServerSideMojo extends AbstractGenerateAPIMojo {

    @Parameter(required = true, alias = "destination-package")
    private String destinationPackage;

    @Parameter(required = true, alias = "types-package")
    private String typesPackage;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RamlModelResult ramlModel = this.resolveRamlModel();
        try {
            new ProcessorGenerator(
                    this.destinationPackage,
                    this.typesPackage + ".types",
                    this.typesPackage,
                    this.outputDirectory
            ).generate(ramlModel);
        } catch (IOException e) {
            throw new MojoExecutionException("error generating processor from raml model", e);
        }
    }
}
