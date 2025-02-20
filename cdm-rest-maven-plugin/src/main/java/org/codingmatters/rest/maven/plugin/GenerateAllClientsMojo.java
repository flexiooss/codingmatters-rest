package org.codingmatters.rest.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codingmatters.rest.api.generator.ClientHandlerImplementation;
import org.codingmatters.rest.api.generator.ClientInterfaceGenerator;
import org.codingmatters.rest.api.generator.ClientRequesterImplementation;
import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.rest.js.api.client.JSClientGenerator;
import org.codingmatters.rest.php.api.client.PhpClientRequesterGenerator;
import org.codingmatters.rest.php.api.client.generator.ComposerFileGenerator;
import org.codingmatters.rest.php.api.client.model.ApiGeneratorPhp;
import org.codingmatters.rest.php.api.client.model.ApiTypesPhpGenerator;
import org.codingmatters.value.objects.generation.Naming;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.php.generator.SpecPhpGenerator;
import org.codingmatters.value.objects.spec.Spec;
import org.raml.v2.api.RamlModelResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mojo(name = "generate-all-clients")
public class GenerateAllClientsMojo extends AbstractGenerateAPIMojo {

    @Parameter(defaultValue = "${basedir}/target")
    private File outputDirectory;

    @Parameter(required = true, alias = "root-package")
    private String rootPackage;

    @Parameter(required = true)
    private String vendor;

    @Parameter(defaultValue = "${project.artifactId}")
    private String artifactId;

    @Parameter(defaultValue = "${project.version}")
    private String version;

    @Parameter(alias = "raml-list")
    private String[] ramlList;

    @Parameter(defaultValue = "${basedir}")
    private String baseDir;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "true", readonly = true, alias = "generate-descriptor")
    private boolean generateDescriptor;

    private String jsVersion;
    private String typesPackage;
    private String clientPackage;
    private String apiPackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        initPackages();
        if( ramlList != null ){
            List<RamlModelResult> collect = new ArrayList<>();
            for( String ramlFile : ramlList ){
                RamlModelResult ramlModelResult = parseFile( ramlFile );
                collect.add( ramlModelResult );
                generateJavaClient( ramlModelResult );
                generatePhpClient( ramlModelResult );
            }
            generateJSClients( collect.toArray( new RamlModelResult[0] ) );
        } else {
            RamlModelResult ramlModel = this.resolveRamlModel();
            generateJSClient( ramlModel );
            generateJavaClient( ramlModel );
            generatePhpClient( ramlModel );
        }
    }

    private void initPackages() {
        this.clientPackage = this.rootPackage + ".client";
        this.apiPackage = this.rootPackage + ".api";
        this.typesPackage = this.rootPackage + ".api.types";
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory( new File( baseDir ) );
        System.out.println( "Trying to convert version from maven to package with flexio-flow" );
        processBuilder.command( "flexio-flow", "convert", "--version", version, "--from", "maven", "--to", "package" );
        try{
            processBuilder.start().waitFor( 30, TimeUnit.SECONDS );
            this.jsVersion = getOutput( processBuilder );
            System.out.println( "Flexio-flow returned js version: " + jsVersion );
        } catch( Exception e ) {
            System.out.println( "Cannot get js version from flexio-flow, using default version: " + version );
            this.jsVersion = version;
        }
    }

    private String getOutput( ProcessBuilder processBuilder ) throws IOException, InterruptedException {
        Process process = processBuilder.start();
        processBuilder.start();
        try( BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) ){
            StringJoiner sj = new StringJoiner( System.getProperty( "line.separator" ) );
            reader.lines().iterator().forEachRemaining( sj::add );
            process.waitFor( 5, TimeUnit.SECONDS );
            if( process.exitValue() != 0 ){
                throw new IOException( "Bad exit code " + process.exitValue() );
            }
            return sj.toString();
        } finally {
            process.destroy();
        }
    }

    private void generatePhpClient( RamlModelResult ramlModel ) throws MojoExecutionException {
        File phpOutputDirectory = new File( outputDirectory, "php-generated-client" );
        boolean useTypeHintingReturnValue = false;
        try{
            Spec spec = new ApiTypesPhpGenerator( typesPackage ).generate( ramlModel );
            new SpecPhpGenerator( spec, typesPackage, phpOutputDirectory, useTypeHintingReturnValue ).generate();
        } catch( RamlSpecException | IOException e ) {
            throw new MojoExecutionException( "Error generating php client", e );
        }
        try{
            Spec spec = new ApiGeneratorPhp( typesPackage ).generate( ramlModel );
            new SpecPhpGenerator( spec, apiPackage, phpOutputDirectory, useTypeHintingReturnValue ).generate();
        } catch( RamlSpecException | IOException e ) {
            throw new MojoExecutionException( "Error generating php client", e );
        }
        try{
            PhpClientRequesterGenerator requesterGenerator = new PhpClientRequesterGenerator( clientPackage, apiPackage, typesPackage, phpOutputDirectory, useTypeHintingReturnValue );
            requesterGenerator.generate( ramlModel );
        } catch( RamlSpecException | IOException e ) {
            throw new MojoExecutionException( "Error generating php client", e );
        }
        try{
            ComposerFileGenerator requesterGenerator = new ComposerFileGenerator( phpOutputDirectory, vendor, artifactId, version );
            requesterGenerator.generateComposerFile();
        } catch( Exception e ) {
            throw new MojoExecutionException( "Error generating php client", e );
        }
        try{
            zipPhpClient( phpOutputDirectory );
        } catch( Exception e ) {
            throw new MojoExecutionException( "Error generating php client", e );
        }

    }

    private void zipPhpClient( File fileToZip ) throws IOException {
        try( FileOutputStream fos = new FileOutputStream( new File( fileToZip.getParentFile(), "php-generated-client.zip" ) ) ){
            try( ZipOutputStream zipOut = new ZipOutputStream( fos ) ){
                zipFile( fileToZip, null, zipOut );
            }
        }
    }

    private void generateJavaClient( RamlModelResult ramlModel ) throws MojoExecutionException {
        File javaOutputDirectory = new File( this.outputDirectory, "generated-sources" );
        try{
            new ClientInterfaceGenerator( clientPackage, apiPackage, javaOutputDirectory ).generate( ramlModel );
        } catch( IOException e ) {
            throw new MojoExecutionException( "error generating client interface from raml model", e );
        }
        try{
            new ClientRequesterImplementation( clientPackage, apiPackage, typesPackage, javaOutputDirectory ).generate( ramlModel );
        } catch( IOException e ) {
            throw new MojoExecutionException( "error generating requester client implementation from raml model", e );
        }
        try{
            new ClientHandlerImplementation( clientPackage, apiPackage, typesPackage, javaOutputDirectory ).generate( ramlModel );
        } catch( IOException e ) {
            throw new MojoExecutionException( "error generating handler client implementation from raml model", e );
        }

        if(this.generateDescriptor) {
            if(this.ramlList != null) {
                for (String raml : this.ramlList) {
                    this.generateDescriptor(ramlModel, raml);
                }
            } else {
                String ramlResource;
                try {
                    ramlResource = this.ramlResource();
                } catch (MojoFailureException e) {
                    throw new MojoExecutionException("error generating descriptor from raml model", e);
                }
                this.generateDescriptor(ramlModel, ramlResource);
            }
        }
    }

    private void generateDescriptor(RamlModelResult ramlModel, String ramlResource) throws MojoExecutionException {
        String descriptor = String.format("""
                        {
                          "root-package": "%s",
                          "api-spec-resource": "%s"
                        }
                        """,
                this.rootPackage,
                ramlResource
        );
        File descriptorFile = new File(this.outputDirectory, "client-descriptors/" + new Naming().apiName(ramlModel.getApiV10().title().value()) + ".json");
        try {
            descriptorFile.getParentFile().mkdirs();
            descriptorFile.createNewFile();
            try (Writer out = new FileWriter(descriptorFile)) {
                out.write(descriptor);
                out.flush();
            }
        } catch (IOException e) {
            throw new MojoExecutionException("error writing descriptor from raml model", e);
        }
    }

    private void generateJSClients( RamlModelResult[] ramlModel ) throws MojoExecutionException {
        try{
            File jsOutputDir = new File( this.outputDirectory, "js-generated-client" );
            JSClientGenerator generator = new JSClientGenerator( jsOutputDir, clientPackage, typesPackage, apiPackage, vendor, artifactId, version );
            generator.generateClientApi( ramlModel );
        } catch( ProcessingException | GenerationException e ) {
            throw new MojoExecutionException( "Error generating JS client", e );
        }
    }

    private void generateJSClient( RamlModelResult ramlModel ) throws MojoExecutionException {
        try{
            File jsOutputDir = new File( this.outputDirectory, "js-generated-client" );
            JSClientGenerator generator = new JSClientGenerator( jsOutputDir, clientPackage, typesPackage, apiPackage, vendor, artifactId, version );
            generator.generateClientApi( ramlModel );
        } catch( ProcessingException | GenerationException e ) {
            throw new MojoExecutionException( "Error generating JS client", e );
        }
    }

    private static void zipFile( File fileToZip, String fileName, ZipOutputStream zipOut ) throws IOException {
        if( fileToZip.isHidden() ){
            return;
        }
        if( fileToZip.isDirectory() ){
            if( fileName != null ){
                if( fileName.endsWith( "/" ) ){
                    zipOut.putNextEntry( new ZipEntry( fileName ) );
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry( new ZipEntry( fileName + "/" ) );
                    zipOut.closeEntry();
                }
            }
            File[] children = fileToZip.listFiles();
            for( File childFile : children ){
                if( fileName != null ){
                    zipFile( childFile, fileName + "/" + childFile.getName(), zipOut );
                } else {
                    zipFile( childFile, childFile.getName(), zipOut );
                }
            }
            return;
        }
        FileInputStream fis = new FileInputStream( fileToZip );
        ZipEntry zipEntry = new ZipEntry( fileName );
        zipOut.putNextEntry( zipEntry );
        byte[] bytes = new byte[1024];
        int length;
        while( (length = fis.read( bytes )) >= 0 ){
            zipOut.write( bytes, 0, length );
        }
        fis.close();
    }

}
