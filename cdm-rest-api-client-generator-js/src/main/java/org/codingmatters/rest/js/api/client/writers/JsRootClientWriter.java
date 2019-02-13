package org.codingmatters.rest.js.api.client.writers;

import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;

import java.io.IOException;
import java.util.List;

public class JsRootClientWriter extends JsFileWriter {

    private final List<ResourceClientDescriptor> clientDescriptors;
    private final String className;
    private final String apiPackage;

    public JsRootClientWriter( String filePath, String className, List<ResourceClientDescriptor> clientDescriptors, String apiPackage ) {
        super( filePath );
        this.className = className;
        this.clientDescriptors = clientDescriptors;
        this.apiPackage = apiPackage;
    }

    public void generateRootClient() throws IOException {
        line( "class " + className + " {" );
        generateConstructor();
        for( ResourceClientDescriptor clientDescriptor : clientDescriptors ){
            generateClientDescriptorFunction( clientDescriptor );
        }
        line( "}" );
        line( "export { " + className + "}" );
    }

    private void generateConstructor() throws IOException {
        line( "/**" );
        line( "* @constructor" );
        line( "* @param {string} gatewayUrl" );
        line( "*/" );
        line( "constrcutor( requester, gatewayUrl ) {" );
        for( ResourceClientDescriptor clientDescriptor : clientDescriptors ){
            line( "this." + NamingUtility.propertyName( clientDescriptor.getClassName() ) + " = new " + NamingUtility.classFullName( apiPackage + "." + clientDescriptor.getClassName() ) + "( requester, gatewayUrl );" );
        }
        line( "}" );
    }

    private void generateClientDescriptorFunction( ResourceClientDescriptor clientDescriptor ) throws IOException {
        line( NamingUtility.propertyName( clientDescriptor.getClassName() ) + "() {" );
        line( "return this." + NamingUtility.propertyName( clientDescriptor.getClassName() ) + ";" );
        line( "}" );
    }

}
