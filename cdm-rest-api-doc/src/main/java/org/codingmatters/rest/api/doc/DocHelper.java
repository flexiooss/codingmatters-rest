package org.codingmatters.rest.api.doc;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;

public class DocHelper {
    static private final String SEQUENCE_PUML_SUFFIX = "-sequence.puml";
    static private final String SEQUENCE_SVG_SUFFIX = "-sequence.svg";

    static public String camelCased(String str) {
        StringBuilder result = new StringBuilder();
        for (String part : str.split("\\s+")) {
            result.append(capitalizedFirst(part));
        }
        return result.toString();
    }

    static public String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }



    static public File overallSvgSequenceFile(RamlModelResult ramlModel, File toDirectory) {
        return new File(toDirectory, camelCased(ramlModel.getApiV10().title().value()) + SEQUENCE_SVG_SUFFIX);
    }

    static public File resourceSvgSequenceFile(RamlModelResult ramlModel, Resource resource, File toDirectory) {
        return new File(toDirectory, camelCased(
                ramlModel.getApiV10().title().value()) + "-" +
                resource.displayName().value() +
                SEQUENCE_SVG_SUFFIX);
    }

    static public File typesSvgClassFile(RamlModelResult ramlModel, File toDirectory) {
        return new File(toDirectory, camelCased(
                ramlModel.getApiV10().title().value()) + ".classes.svg"
        );
    }

    static public File typesSvgClassFile(RamlModelResult ramlModel, TypeDeclaration type, File toDirectory) {
        return new File(toDirectory, camelCased(
                ramlModel.getApiV10().title().value()) + "." + type.name() + ".classes.svg"
        );
    }

    static public File overallSequenceFile(RamlModelResult ramlModel, File toDirectory) {
        return new File(toDirectory, camelCased(ramlModel.getApiV10().title().value()) + SEQUENCE_PUML_SUFFIX);
    }

    static public File resourceSequenceFile(RamlModelResult ramlModel, Resource resource, File toDirectory) {
        return new File(toDirectory, camelCased(
                ramlModel.getApiV10().title().value()) + "-" +
                resource.displayName().value() +
                SEQUENCE_PUML_SUFFIX);
    }

    static public String markdownToHtml(String str) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(str);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
