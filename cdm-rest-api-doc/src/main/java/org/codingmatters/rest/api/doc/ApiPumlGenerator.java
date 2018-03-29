package org.codingmatters.rest.api.doc;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.codingmatters.value.objects.FormattedWriter;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ApiPumlGenerator {

    private final RamlModelResult ramlModel;
    private final File toDirectory;

    public ApiPumlGenerator(RamlModelResult ramlModel, File toDirectory) {
        this.ramlModel = ramlModel;
        this.toDirectory = toDirectory;
    }

    public void generate() throws IOException {
        this.toDirectory.mkdirs();
        this.generateOverallPuml();
        this.generatePerResourceFiles(this.ramlModel.getApiV10().resources());
    }

    private void generateOverallPuml() throws IOException {
        File sequenceFile = DocHelper.overallSequenceFile(this.ramlModel, this.toDirectory);
        sequenceFile.createNewFile();

        try(FormattedWriter out = new FormattedWriter(new FileWriter(sequenceFile))) {
            this.generateHeader(out);
            this.generateSequences(this.ramlModel.getApiV10().resources(), out);
            this.generateFooter(out);
        }

        this.generateSvg(sequenceFile);
    }

    private void generatePerResourceFiles(List<Resource> resources) throws IOException {
        for (Resource resource : resources) {
            this.generatePerResourceFile(resource);
            this.generatePerResourceFiles(resource.resources());
        }
    }

    private void generatePerResourceFile(Resource resource) throws IOException {
        File sequenceFile = DocHelper.resourceSequenceFile(this.ramlModel, resource, this.toDirectory);
        sequenceFile.createNewFile();

        try(FormattedWriter out = new FormattedWriter(new FileWriter(sequenceFile))) {
            this.generateHeader(out);
            this.generateSequence(resource, out);
            this.generateFooter(out);
        }

        this.generateSvg(sequenceFile);
    }

    private void generateHeader(FormattedWriter out) throws IOException {
        out.appendLine("@startuml");
        out.appendLine("participant \"%s\" as api", this.ramlModel.getApiV10().title().value());
    }


    private void generateFooter(FormattedWriter out) throws IOException {
        out.appendLine("@enduml");
    }

    private void generateSequences(List<Resource> resources, FormattedWriter out) throws IOException {
        for (Resource resource : resources) {
            this.generateSequence(resource, out);
            this.generateSequences(resource.resources(), out);
        }
    }

    private void generateSequence(Resource resource, FormattedWriter out) throws IOException {
        out.appendLine("");
        out.appendLine("== %s ==", resource.displayName().value());
        for (Method method : resource.methods()) {
            out.appendLine("");
            this.generateRequest(resource, out, method);
            out.appendLine("activate api");

            if(method.responses() != null && ! method.responses().isEmpty()) {
                if (method.responses().size() == 1) {
                    this.generateResponse(out, method.responses().get((0)), "");
                } else {
                    out.appendLine("alt");
                    boolean started = false;
                    for (Response response : method.responses()) {
                        if(started) {
                            out.appendLine("else");
                        }
                        this.generateResponse(out, response, "   ");
                        started = true;
                    }
                    out.appendLine("end");
                }
            }

            out.appendLine("deactivate api");
        }
    }

    private void generateRequest(Resource resource, FormattedWriter out, Method method) throws IOException {
        out.appendLine("--> api: <b>%s</b> %s %s%s",
                method.method().toUpperCase(),
                this.resourceUrl(resource, method),
                this.generateHeaders(method.headers()),
                this.requestBodyPartsAsString(method.body())
        );
    }

    private void generateResponse(FormattedWriter out, Response response, String prefix) throws IOException {
        out.appendLine("%s<- api: %s: %s %s",
                prefix,
                response.code().value(),
                this.generateHeaders(response.headers()),
                this.responseBodyPartsAsString(response.body())
        );
    }

    private StringBuilder generateHeaders(List<TypeDeclaration> headers) {
        StringBuilder result = new StringBuilder();
        if(headers != null && ! headers.isEmpty()) {
            result.append("[");
            for (TypeDeclaration header : headers) {
                result.append("\\n\\t\\t").append("<b>").append(header.name()).append("</b>").append("=");
                if(header instanceof ArrayTypeDeclaration) {
                    result.append(((ArrayTypeDeclaration)header).items().type()).append("[]");
                } else {
                    result.append(header.type());
                }
            }
            result.append("\\n\\t\\t]");
        }
        return result;
    }

    private String resourceUrl(Resource resource, Method method) {
        String path = resource.resourcePath();
        if(resource.uriParameters() != null && ! resource.uriParameters().isEmpty()) {
            for (TypeDeclaration uriParam : resource.uriParameters()) {
                path = path.replaceAll("\\{" + uriParam.name() + "\\}", "<b>" + uriParam.name() + "</b>");
            }

        }

        StringBuilder result = new StringBuilder(path);
        if(method.queryParameters() != null && ! method.queryParameters().isEmpty()) {
            boolean started = false;
            for (TypeDeclaration parameter : method.queryParameters()) {
                if(started) {
                    result.append("&\\n\\t\\t");
                } else {
                    result.append("?\\n\\t\\t");
                }
                started = true;

                result.append("<b>").append(parameter.name()).append("</b>").append("=");
                if(parameter instanceof ArrayTypeDeclaration) {
                    result.append(((ArrayTypeDeclaration)parameter).items().type()).append("[]");
                } else {
                    result.append(parameter.type());
                }
            }
        }
        return result.toString();
    }

    private StringBuilder requestBodyPartsAsString(List<TypeDeclaration> bodyParts) {
        StringBuilder body = new StringBuilder();
        if(bodyParts != null && ! bodyParts.isEmpty()) {
            for (TypeDeclaration bodyType : bodyParts) {
                if(bodyType instanceof ArrayTypeDeclaration) {
                    body.append("\\n\\t<b>[").append(((ArrayTypeDeclaration)bodyType).items().type()).append("]</b>");
                } else {
                    body.append("\\n\\t<b>").append(bodyType.type()).append("</b>");
                }
            }
        }
        return body;
    }

    private StringBuilder responseBodyPartsAsString(List<TypeDeclaration> bodyParts) {
        StringBuilder body = new StringBuilder();
        if(bodyParts != null && ! bodyParts.isEmpty()) {
            boolean started = false;
            for (TypeDeclaration bodyType : bodyParts) {
                if(started) {
                    body.append(", ");
                }
                body.append("<b>").append(bodyType.type()).append("</b>");
            }
        }
        return body;
    }



    private void generateSvg(File pumlFile) throws IOException {
        SourceFileReader sourceFileReader = new SourceFileReader(
                pumlFile,
                pumlFile.getParentFile(),
                new FileFormatOption(FileFormat.SVG));
        if(sourceFileReader.hasError()) {
            for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
                System.err.println(generatedImage);
            }
            throw new AssertionError("failed generating svg from puml : " + pumlFile.getAbsolutePath());
        }
        for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
            System.out.println("generated : " + generatedImage);
        }
    }
}
