package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.api.generator.ApiGenerator;
import org.codingmatters.rest.api.generator.ApiTypesGenerator;
import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.php.api.client.PhpClientRequesterGenerator;
import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.rest.php.api.client.model.ApiTypesPhpGenerator;
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
            generateClient( ramlModel );
        } catch( RamlSpecException e ) {
            throw new MojoExecutionException( "Something went wrong while generating specification", e );
        } catch( IOException e ) {
            throw new MojoFailureException( "Something went wrong while generating php client", e );
        }
    }

    private void generateClient( RamlModelResult ramlModel ) throws IOException, RamlSpecException {
        String clientPackage = destinationPackage + ".client";
        String apiPackage = destinationPackage + ".api";
        String typesPackage = destinationPackage + ".types";
        PhpClientRequesterGenerator requesterGenerator = new PhpClientRequesterGenerator( clientPackage, apiPackage, typesPackage, outputDirectory );
        requesterGenerator.generate( ramlModel );

    }

    private void generateApi( RamlModelResult ramlModel ) throws RamlSpecException, IOException {
        String typesPackage = this.destinationPackage + ".types";
        Spec spec = new ApiGeneratorPhp( typesPackage ).generate( ramlModel );
        new SpecPhpGenerator( spec, typesPackage, this.outputDirectory ).generate();

    }

    private void generateTypes( RamlModelResult ramlModel ) throws RamlSpecException, IOException {
        String typesPackage = this.destinationPackage + ".types";
        Spec spec = new ApiTypesPhpGenerator( typesPackage ).generate( ramlModel );
        new SpecPhpGenerator( spec, typesPackage, this.outputDirectory ).generate();
    }

}
