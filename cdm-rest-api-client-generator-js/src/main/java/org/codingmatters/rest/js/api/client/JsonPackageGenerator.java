package org.codingmatters.rest.js.api.client;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;

import java.io.File;

public class JsonPackageGenerator {

    private final File rootDirectory;

    public JsonPackageGenerator( File rootDirectory ) {
        this.rootDirectory = rootDirectory;
    }

    public void generatePackageJson( String vendor, String artifactId, String version ) throws ProcessingException {
        try( JsFileWriter write = new JsFileWriter( new File( rootDirectory, "package.json" ).getPath() ) ) {
            write.line( "{" );
            write.line( "\"name\": \"@" + vendor + "/" + artifactId + "\"," );
            write.line( "\"version\": \"" + version + "\"," );
            write.line( "\"dependencies\": {" );
            write.line( "\"flexio-jshelpers\": \"https://github.com/flexiooss/flexio-jshelpers.git\"" );
            write.unindent();
            write.line( "}," );
            write.line( "\"main\": \"io/packages.js\"" );
            write.line( "}" );
            write.flush();
        } catch( Exception e ){
            throw new ProcessingException( "Error generating package.json file", e );
        }
    }

}
