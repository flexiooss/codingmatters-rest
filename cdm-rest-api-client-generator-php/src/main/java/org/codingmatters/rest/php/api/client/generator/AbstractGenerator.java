package org.codingmatters.rest.php.api.client.generator;

import java.io.BufferedWriter;
import java.io.IOException;

public class AbstractGenerator {

    private final String indent = "    ";

    protected void twoLine( BufferedWriter writer, int indent ) throws IOException {
        newLine( writer, 0 );
        newLine( writer, indent );
    }

    protected void newLine( BufferedWriter writer, int indent ) throws IOException {
        writer.newLine();
        indent( writer, indent );
    }

    protected void indent( BufferedWriter writer, int indentSize ) throws IOException {
        for( int i = 0; i < indentSize; i++ ) {
            writer.write( this.indent );
        }
    }
}
