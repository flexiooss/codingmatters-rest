package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.HandlersGenerator;
import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

/**
 * Created by nelt on 5/3/17.
 */
@Mojo(name = "generate-api-types")
public class GenerateAPITypesMojo extends AbstractGenerateAPIMojo {

    @Parameter(required = true, alias = "destination-package")
    private String destinationPackage;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RamlModelResult ramlModel = this.resolveRamlModel();

        this.generateTypes(ramlModel);
        this.generateApi(ramlModel);
    }


    private void generateTypes(RamlModelResult ramlModel) throws MojoExecutionException {
        try {
            Spec spec = new ApiTypesGenerator().generate(ramlModel);
            getLog().debug("Types Spec : " + spec);
            this.generateCodeFromSpec(spec, this.destinationPackage + ".types");
        } catch (RamlSpecException e) {
            throw new MojoExecutionException("error generating value object spec from raml api types", e);
        }
    }

    private void generateApi(RamlModelResult ramlModel) throws MojoExecutionException {
        try {
            Spec spec = new ApiGenerator(this.destinationPackage + ".types").generate(ramlModel);
            getLog().debug("API Spec : " + spec);
            this.generateCodeFromSpec(spec, this.destinationPackage);
        } catch (RamlSpecException e) {
            throw new MojoExecutionException("error generating value object spec from raml api types", e);
        }

        try {
            new HandlersGenerator(
                    this.destinationPackage,
                    this.destinationPackage + ".types",
                    this.destinationPackage,
                    this.outputDirectory
            ).generate(ramlModel);
        } catch (IOException e) {
            throw new MojoExecutionException("error generating handlers from raml model", e);
        }
    }

    private void generateCodeFromSpec(Spec spec, String packageName) throws MojoExecutionException {
        try {
            new SpecCodeGenerator(spec, packageName, this.outputDirectory).generate();
            new JsonFrameworkGenerator(spec, packageName, this.outputDirectory).generate();
        } catch (IOException e) {
            throw new MojoExecutionException("error generating code from spec", e);
        }
    }

}
