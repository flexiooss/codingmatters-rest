package org.codingmatters.rest.js.api.client.writers;

import org.codingmatters.rest.php.api.client.model.ResourceClientDescriptor;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;

import java.io.IOException;
import java.util.List;

public class JsRootClientWriter extends JsFileWriter {

    private final List<ResourceClientDescriptor> clientDescriptors;
    private final String className;

    public JsRootClientWriter( String filePath, String apiName, List<ResourceClientDescriptor> clientDescriptors ) {
        super( filePath );
        this.className = apiName + "Client";
        this.clientDescriptors = clientDescriptors;
    }

    public void generateRootClient() throws IOException {
        line( "class " + className + " {" );
        generateConstructor();
        for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
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
        for( ResourceClientDescriptor clientDescriptor : clientDescriptors ) {
            line( "this." + NamingUtility.propertyName( clientDescriptor.getClassName() ) + " = new " + clientDescriptor.getClassName() + "( requester, gatewayUrl );" );
        }
        line( "}" );
    }

    private void generateClientDescriptorFunction( ResourceClientDescriptor clientDescriptor ) throws IOException {
        line( NamingUtility.propertyName( clientDescriptor.getClassName() ) + "() {" );
        line( "return this." + NamingUtility.propertyName( clientDescriptor.getClassName() ) + ";" );
        line( "}" );
    }

}
