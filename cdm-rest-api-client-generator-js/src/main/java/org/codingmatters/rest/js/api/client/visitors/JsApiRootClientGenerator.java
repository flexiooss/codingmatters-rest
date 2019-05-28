package org.codingmatters.rest.js.api.client.visitors;

import org.codingmatters.rest.parser.model.ParsedRaml;
import org.codingmatters.rest.parser.model.ParsedRequest;
import org.codingmatters.rest.parser.model.ParsedResponse;
import org.codingmatters.rest.parser.model.ParsedRoute;
import org.codingmatters.rest.parser.model.typed.TypedBody;
import org.codingmatters.rest.parser.model.typed.TypedHeader;
import org.codingmatters.rest.parser.model.typed.TypedQueryParam;
import org.codingmatters.rest.parser.model.typed.TypedUriParams;
import org.codingmatters.rest.parser.processing.ParsedRamlProcessor;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;

import java.io.File;
import java.io.IOException;

public class JsApiRootClientGenerator implements ParsedRamlProcessor {

    private final File rootDirectory;
    private final String clientPackage;
    private final String resourcesPackage;
    private final PackageFilesBuilder packageBuilder;
    private JsFileWriter write;

    public JsApiRootClientGenerator( File rootDirectory, String clientPackage, String resourcesPackage, PackageFilesBuilder packageBuilder ) {
        this.rootDirectory = rootDirectory;
        this.clientPackage = clientPackage;
        this.resourcesPackage = resourcesPackage;
        this.packageBuilder = packageBuilder;
    }

    @Override
    public void process( ParsedRaml parsedRaml ) throws ProcessingException {
        String className = NamingUtility.className( parsedRaml.apiName(), "Client" );
        try( JsFileWriter write = new JsFileWriter( rootDirectory + "/" + clientPackage.replace( ".", "/" ) + "/" + className + ".js" ) ) {
            this.write = write;
            packageBuilder.addList( clientPackage, className );
            write.line( "import { globalScope, FLEXIO_IMPORT_OBJECT } from 'flexio-jshelpers'" );
            write.line( "class " + className + "{" );
            write.line( "/**" );
            write.line( "* @returns string" );
            write.line( "*/" );
            write.line( "apiName() {");
            write.line( "return '" + NamingUtility.getApiName( parsedRaml.apiName() ) + "'" );
            write.line( "}" );
            generateConstructor( parsedRaml );
            for( ParsedRoute parsedRoute : parsedRaml.routes() ){
                parsedRoute.process( this );
            }
            write.line( "}" );
            write.line( "export { " + className + " }" );
        } catch( Exception e ){
            throw new ProcessingException( "Error processing root client" );
        }
    }

    private void generateConstructor( ParsedRaml parsedRaml ) throws IOException {
        write.line( "/**" );
        write.line( "* @constructor" );
        write.line( "* @param {string} gatewayUrl" );
        write.line( "* @param {string} gatewayUrl" );
        write.line( "*/" );
        write.line( "constructor(requester, gatewayUrl) {" );
        for( ParsedRoute route : parsedRaml.routes() ){
            write.line( "this._" + NamingUtility.propertyName( route.displayName() ) + " = new " + NamingUtility.classFullName( resourcesPackage + "." + route.displayName() ) + "(requester, gatewayUrl)" );
        }
        write.line( "}" );
    }

    @Override
    public void process( ParsedRoute parsedRoute ) throws ProcessingException {
        try {
            String propertyName = NamingUtility.propertyName( parsedRoute.displayName() );
            String className = NamingUtility.className( parsedRoute.displayName() );
            write.newLine();
            write.line( "/**" );
            write.line( "* @returns {" + className + "}" );
            write.line( "*/" );
            write.line( propertyName + "() {" );
            write.line( "return this._" + propertyName );
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing route", e );
        }
    }

}
