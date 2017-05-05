package org.codingmatters.http.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.http.api.generator.ApiTypesGenerator;
import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.*;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by nelt on 5/3/17.
 */
@Mojo(name = "generate-api-types")
public class GenerateAPITypesMojo extends AbstractMojo {

    @Parameter( defaultValue = "${plugin}", readonly = true )
    private PluginDescriptor plugin;


    @Parameter(required = true)
    private String destinationPackage;

    @Parameter(name = "api-spec-file")
    private File apiSpecFile;

    @Parameter(name = "api-spec-resource")
    private String apiSpecResource;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File apiFile = this.resolveApiFile();
        this.getLog().info("API : " + apiFile.getAbsolutePath());

        try {
            RamlModelResult ramlModel = new RamlModelBuilder().buildApi(apiFile);
            if(ramlModel.hasErrors()) {
                for (ValidationResult validationResult : ramlModel.getValidationResults()) {
                    this.getLog().error(validationResult.getMessage());
                }
                throw new MojoExecutionException("failed parsing raml api, see logs");
            }
            Spec spec = new ApiTypesGenerator().generate(ramlModel);
            new SpecCodeGenerator(spec, this.destinationPackage, this.outputDirectory).generate();
        } catch (RamlSpecException e) {
            throw new MojoExecutionException("error generating value object spec from raml api", e);
        } catch (IOException e) {
            throw new MojoExecutionException("error generating code from spec", e);
        }

    }

    private File resolveApiFile() throws MojoFailureException {
        if(this.apiSpecFile != null && this.apiSpecFile.exists()) {
            return this.apiSpecFile;
        } else if(this.apiSpecResource != null){
            try {
                Artifact artifact = null;
                for (Artifact anArtifact : this.plugin.getArtifacts()) {
                    try {
                        JarFile jar = new JarFile(anArtifact.getFile().getAbsolutePath());
                        JarEntry entry = jar.getJarEntry(this.apiSpecResource);
                        if (entry != null) {
                            artifact = anArtifact;
                        }
                    } catch (IOException e) {
                        throw new MojoFailureException("error looking up for input specification resource : " + this.apiSpecResource, e);
                    }
                }
                if(artifact == null) {
                    throw new MojoFailureException("input specification resource not found : " + this.apiSpecResource);
                }

                File temp = File.createTempFile("spec", ".raml");
                temp.deleteOnExit();

                URL url = new URL("jar:file:" + artifact.getFile().getAbsolutePath() + "!/" + this.apiSpecResource);
                try(
                        InputStream in = url.openStream() ;
                        Reader reader = new InputStreamReader(in, "UTF-8");
                        Writer writer = new FileWriter(temp);
                ) {
                    char[] buffer = new char[1024];
                    for(int read = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                        writer.write(buffer, 0, read);
                        System.out.println(new String(buffer, 0, read));
                    }
                    writer.flush();
                }
                return temp;
            } catch (IOException e) {
                throw new MojoFailureException("error getting specification file from resource", e);
            }
        } else {
            throw new MojoFailureException("must provide input specification file (" +
                    "file path as inputSpecification or " +
                    "plugin dependency resource as inputSpecificationResource" +
                    ")");
        }
    }
}