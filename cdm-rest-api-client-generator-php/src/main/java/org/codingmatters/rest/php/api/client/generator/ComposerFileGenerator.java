package org.codingmatters.rest.php.api.client.generator;

import org.codingmatters.value.objects.js.error.ProcessingException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ComposerFileGenerator {

    private final String vendor;
    private final String artifactId;
    private final String version;
    private final File rootDir;

    public ComposerFileGenerator( File phpOutputDirectory, String vendor, String artifactId, String version ) {
        this.rootDir = phpOutputDirectory;
        this.vendor = vendor;
        this.artifactId = artifactId;
        this.version = version;
    }

    public void generateComposerFile() throws ProcessingException {
        try( BufferedWriter writer = new BufferedWriter( new FileWriter( new File( rootDir, "composer.json" ) ) ) ) {
            writer.write( "{" );
            writer.newLine();
            writer.write( "  \"name\": \"${flexio-vendor}/${project.artifactId}\"," );
            writer.newLine();
            writer.write( "  \"version\": \"${project.version}\"," );
            writer.newLine();
            writer.write( "  \"require\": {" );
            writer.newLine();
            writer.write( "    \"flexio/utils\": \"master@dev\"" );
            writer.newLine();
            writer.write( "  }," );
            writer.newLine();
            writer.write( "  \"repositories\": [" );
            writer.newLine();
            writer.write( "    {" );
            writer.newLine();
            writer.write( "      \"type\": \"git\"," );
            writer.newLine();
            writer.write( "      \"url\": \"https://github.com/flexiooss/flexio-php-utils.git\"" );
            writer.newLine();
            writer.write( "    }" );
            writer.newLine();
            writer.write( "  ]," );
            writer.newLine();
            writer.write( "  \"autoload\": {" );
            writer.newLine();
            writer.write( "    \"psr-4\": {" );
            writer.newLine();
            writer.write( "      \"io\\\\\": \"io/\"" );
            writer.newLine();
            writer.write( "    }" );
            writer.newLine();
            writer.write( "  }" );
            writer.newLine();
            writer.write( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error generating composer file", e );
        }
    }
}
