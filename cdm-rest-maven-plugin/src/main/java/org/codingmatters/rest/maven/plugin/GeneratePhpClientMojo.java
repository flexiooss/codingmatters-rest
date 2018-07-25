package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.php.generator.SpecPhpGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelResult;

import java.io.File;
import java.io.IOException;

@Mojo(name = "generate-php-client")
public class GeneratePhpClientMojo extends AbstractGenerateAPIMojo {

    @Parameter(required = true, alias = "destination-package")
    private String destinationPackage;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RamlModelResult ramlModel = this.resolveRamlModel();
        generatePhpClientSide( ramlModel );
    }

    private void generatePhpClientSide( RamlModelResult ramlModel ) throws MojoExecutionException, MojoFailureException {
        try {
            generateTypes( ramlModel );
            generateApi( ramlModel );
        } catch( RamlSpecException e ) {
            throw new MojoExecutionException( "Something went wrong while generating specification", e );
        } catch( IOException e ) {
            throw new MojoFailureException( "Something went wrong while generating php client", e );
        }
    }

    private void generateApi( RamlModelResult ramlModel ) throws RamlSpecException, IOException {
        Spec spec = new ApiGenerator( this.destinationPackage + ".types" ).generate( ramlModel );
        new SpecPhpGenerator( spec, this.destinationPackage, this.outputDirectory ).generate();
    }

    private void generateTypes( RamlModelResult ramlModel ) throws RamlSpecException, IOException {
        Spec spec = new ApiTypesGenerator().generate( ramlModel );
        new SpecPhpGenerator( spec, this.destinationPackage, this.outputDirectory ).generate();
    }

}
