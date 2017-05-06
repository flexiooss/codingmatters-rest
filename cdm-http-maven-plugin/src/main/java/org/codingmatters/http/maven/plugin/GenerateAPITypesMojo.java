package org.codingmatters.http.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.http.api.generator.ApiGenerator;
import org.codingmatters.http.api.generator.ApiTypesGenerator;
import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
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


    @Parameter(required = true, alias = "destination-package")
    private String destinationPackage;

    @Parameter(alias = "api-spec-file")
    private File apiSpecFile;

    @Parameter(alias = "api-spec-resource")
    private String apiSpecResource;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File apiFile = this.resolveApiFile();
        this.getLog().info("API : " + apiFile.getAbsolutePath());


        RamlModelResult ramlModel = this.buildRamlModel(apiFile);
        this.generateTypes(ramlModel);
        this.generateApi(ramlModel);
    }

    private void generateTypes(RamlModelResult ramlModel) throws MojoExecutionException {
        try {
            Spec spec = new ApiTypesGenerator().generate(ramlModel);
            this.generateCodeFromSpec(spec, this.destinationPackage + ".types");
        } catch (RamlSpecException e) {
            throw new MojoExecutionException("error generating value object spec from raml api types", e);
        }
    }

    private void generateApi(RamlModelResult ramlModel) throws MojoExecutionException {
        try {
            Spec spec = new ApiGenerator(this.destinationPackage + ".types").generate(ramlModel);
            this.generateCodeFromSpec(spec, this.destinationPackage + ".api");
        } catch (RamlSpecException e) {
            throw new MojoExecutionException("error generating value object spec from raml api types", e);
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

    private File resolveApiFile() throws MojoFailureException {
        if(this.apiSpecFile != null && this.apiSpecFile.exists()) {
            return this.apiSpecFile;
        } else if(this.apiSpecResource != null){
            try {
                Artifact artifact = null;
                for (Artifact anArtifact : this.plugin.getArtifacts()) {
                    try {
                        if(anArtifact.getFile().isFile() && anArtifact.getFile().getName().endsWith(".jar")) {
                            JarFile jar = new JarFile(anArtifact.getFile().getAbsolutePath());
                            JarEntry entry = jar.getJarEntry(this.apiSpecResource);
                            if (entry != null) {
                                artifact = anArtifact;
                                break;
                            }
                        } else if(anArtifact.getFile().isDirectory()) {
                            if(new File(anArtifact.getFile(), this.apiSpecResource).exists()) {
                                artifact = anArtifact;
                                break;
                            }
                        }
                    } catch (IOException e) {
                        throw new MojoFailureException("error looking up for input specification resource : " + this.apiSpecResource, e);
                    }
                }
                if(artifact == null) {
                    throw new MojoFailureException("input specification resource not found : " + this.apiSpecResource);
                }

                if (artifact.getFile().isDirectory()) {
                    return new File(artifact.getFile(), this.apiSpecResource);
                } else {
                    File temp = File.createTempFile("spec", ".raml");
                    temp.deleteOnExit();

                    URL url = new URL("jar:file:" + artifact.getFile().getAbsolutePath() + "!/" + this.apiSpecResource);
                    try (
                            InputStream in = url.openStream();
                            Reader reader = new InputStreamReader(in, "UTF-8");
                            Writer writer = new FileWriter(temp);
                    ) {
                        char[] buffer = new char[1024];
                        for (int read = reader.read(buffer); read != -1; read = reader.read(buffer)) {
                            writer.write(buffer, 0, read);
                        }
                        writer.flush();
                    }
                    return temp;
                }
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
