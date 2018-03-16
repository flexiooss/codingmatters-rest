package org.codingmatters.rest.api.doc;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ApiPumlGeneratorTest {

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public TestName name = new TestName();












    private void generatePng(String name) throws IOException {
        File outputDir = new File("/tmp/exp/" + this.name.getMethodName());
        outputDir.mkdirs();

        SourceFileReader sourceFileReader = new SourceFileReader(new File(this.dir.getRoot(), name), outputDir, "UTF-8");
        if(sourceFileReader.hasError()) {
            for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
                System.err.println(generatedImage);
            }
            System.err.println(this.fileContent(name));


            throw new AssertionError("failed generating png from puml");
        }
        for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
            System.out.println("generated : " + generatedImage);
        }
    }

    private String lines(String ... lines) {
        if(lines == null) return null;
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line).append("\n");
        }

        return result.toString();
    }

    private String fileContent(String name) throws IOException {
        File file = new File(this.dir.getRoot(), name);
        try(ByteArrayOutputStream out = new ByteArrayOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte [] buffer = new byte[1024];
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
            return out.toString();
        }
    }
}