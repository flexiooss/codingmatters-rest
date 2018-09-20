package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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

    @Parameter(defaultValue = "true")
    private boolean useTypeHintingReturnValue;

    private String clientPackage;
    private String apiPackage;
    private String typesPackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        RamlModelResult ramlModel = this.resolveRamlModel();
        generatePhpClientSide( ramlModel );
    }

    private void generatePhpClientSide( RamlModelResult ramlModel ) throws MojoExecutionException, MojoFailureException {
        try {
            this.clientPackage = this.destinationPackage + ".client";
            this.apiPackage = this.destinationPackage + ".api";
            this.typesPackage = this.destinationPackage + ".types";
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
        PhpClientRequesterGenerator requesterGenerator = new PhpClientRequesterGenerator( this.clientPackage, this.apiPackage, this.typesPackage, this.outputDirectory, useTypeHintingReturnValue );
        requesterGenerator.generate( ramlModel );
    }

    private void generateApi( RamlModelResult ramlModel ) throws RamlSpecException, IOException {
        Spec spec = new ApiGeneratorPhp( this.typesPackage ).generate( ramlModel );
        new SpecPhpGenerator( spec, this.apiPackage, this.outputDirectory, useTypeHintingReturnValue ).generate();

    }

    private void generateTypes( RamlModelResult ramlModel ) throws RamlSpecException, IOException {
        Spec spec = new ApiTypesPhpGenerator( this.typesPackage ).generate( ramlModel );
        new SpecPhpGenerator( spec, this.typesPackage, this.outputDirectory, useTypeHintingReturnValue ).generate();
    }

}
