package org.codingmatters.rest.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.rest.maven.plugin.raml.RamlFileCollector;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * Created by nelt on 5/17/17.
 */
public abstract class AbstractGenerateAPIMojo extends AbstractMojo {

    @Parameter( defaultValue = "${plugin}", readonly = true )
    private PluginDescriptor plugin;

    @Parameter(alias = "api-spec-file")
    private File apiSpecFile;

    @Parameter(alias = "api-spec-resource")
    private String apiSpecResource;

    protected RamlModelResult resolveRamlModel() throws MojoFailureException, MojoExecutionException {
        String resource;
        if(this.apiSpecFile != null) {
            resource = this.apiSpecFile.getAbsolutePath();
        } else if(this.apiSpecResource != null) {
            resource = this.apiSpecResource;
        } else {
            throw new MojoFailureException("must provide path to the RAML spec, either as a file in apiSpecFile property, or as a classpath resource with apiSpecResource property.");
        }

        RamlFileCollector.Builder builder = RamlFileCollector.spec(resource);
        try {
            this.appendArtifactsJars(builder);
        } catch (IOException e) {
            throw new MojoFailureException("error crawling project artifacts", e);
        }
        try(RamlFileCollector collector = builder.build()) {
            File apiFile = collector.specFile();
            this.getLog().info("API : " + apiFile.getAbsolutePath());
            return this.buildRamlModel(apiFile);
        } catch (Exception e) {
            throw new MojoFailureException("error resolving RAML model files", e);
        }
    }

    private RamlModelResult buildRamlModel(File apiFile) throws MojoExecutionException {
        RamlModelResult ramlModel;
        ramlModel = new RamlModelBuilder().buildApi(apiFile);
        if(ramlModel.hasErrors()) {
            for (ValidationResult validationResult : ramlModel.getValidationResults()) {
                this.getLog().error(validationResult.getMessage());
            }
            throw new MojoExecutionException("failed parsing raml api, see logs");
        }
        return ramlModel;
    }


    private void appendArtifactsJars(RamlFileCollector.Builder builder) throws IOException {
        for (Artifact anArtifact : this.plugin.getArtifacts()) {
            if (anArtifact.getFile().isFile() && anArtifact.getFile().getName().endsWith(".jar")) {
                JarFile jar = new JarFile(anArtifact.getFile().getAbsolutePath());
                builder.classpathJar(jar);
            }
        }
    }
}
