package org.codingmatters.rest.api.doc;

import org.codingmatters.value.objects.FormattedWriter;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.*;
import java.util.List;

import static org.codingmatters.rest.api.doc.DocHelper.*;

public class ApiHtmlDocGenerator {

    private final RamlModelResult ramlModel;
    private final File toDirectory;

    public ApiHtmlDocGenerator(RamlModelResult ramlModel, File toDirectory) {
        this.ramlModel = ramlModel;
        this.toDirectory = toDirectory;
    }

    public void generate() throws IOException {
        this.toDirectory.mkdirs();

        File htmlFile = new File(this.toDirectory, camelCased(ramlModel.getApiV10().title().value()) + "-api.html");
        htmlFile.createNewFile();

        try(FormattedWriter html = new FormattedWriter(new FileWriter(htmlFile))) {
            html
                    .appendLine("<!DOCTYPE html>")
                    .appendLine("<html lang=\"en\">")
                    .appendLine("  <head>")
                    .appendLine("    <meta charset=\"utf-8\">")
                    .appendLine("    <title>%s</title>", ramlModel.getApiV10().title().value())
                    .appendLine("  </head>")
                    .appendLine("  <body>")
                    .appendLine("    <main>")
            ;

            this.appendHeader(html, "      ");
            this.appendResourceArticles(this.ramlModel.getApiV10().resources(), html, "      ");
            this.appendOverviewArticle(html, "      ");
            this.appendTypesArticle(html, "      ");

            html
                    .appendLine("    </main>")
                    .appendLine("  </body>")
                    .appendLine("</html>")
            ;
        }
    }

    private void appendHeader(FormattedWriter html, String prefix) throws IOException {
        html
                .appendLine("%s<header>", prefix)
                .appendLine("%s  <nav>", prefix)
                .appendLine("%s    <ul>", prefix)
                .appendLine("%s      <li>", prefix)
                .appendLine("%s        Resources", prefix)
                ;

        this.appendResourceNav(this.ramlModel.getApiV10().resources(), html, prefix+ "          ");
        html
                .appendLine("%s      </li>", prefix)
                ;

        if(! this.ramlModel.getApiV10().types().isEmpty()) {
            html
                    .appendLine("%s      <li><a href=\"#api-types\">API Types</a>", prefix)
                    .appendLine("%s        <nav>", prefix)
                    .appendLine("%s          <ul>", prefix)
                    ;
            for (TypeDeclaration type : this.ramlModel.getApiV10().types()) {
                html.appendLine("%s          <li><a href=\"#%s-type\">%s</a></li>", prefix, type.name(), type.name());
            }
            html.appendLine("%s          <li><a href=\"#all-types\">All types</a></li>", prefix);
            html
                    .appendLine("%s          </ul>", prefix)
                    .appendLine("%s        </nav>", prefix)
                    .appendLine("%s      </li>", prefix);
        }

        html
                .appendLine("%s      <li><a href=\"#overview\">Overview</a></li>", prefix)
                .appendLine("%s    </ul>", prefix)
                .appendLine("%s  </nav>", prefix)
                .appendLine("%s</header>", prefix)
                ;
    }

    private void appendResourceNav(List<Resource> resources, FormattedWriter html, String prefix) throws IOException {
        if(resources.isEmpty()) return;

        html
                .appendLine("%s<nav>", prefix)
                .appendLine("%s  <ul>", prefix)
                ;

        for (Resource resource : resources) {
            html.appendLine("%s    <li>", prefix);
            html.appendLine("%s      <a href=\"#%s\">%s</a>", prefix, camelCased(resource.displayName().value()) + "-resource", resource.displayName().value());
            this.appendResourceNav(resource.resources(), html, prefix + "      ");
            html.appendLine("%s    </li>", prefix);
        }


        html
                .appendLine("%s  </ul>", prefix)
                .appendLine("%s</nav>", prefix)
        ;
    }

    private void appendOverviewArticle(FormattedWriter html, String prefix) throws IOException {
        html
                .appendLine("%s<article id=\"%s\">", prefix, "overview")
                .appendLine("%s  <header>", prefix)
                .appendLine("%s    <h1>Overview</h1>", prefix)
                .appendLine("%s  </header>", prefix)
                .appendLine("%s  <section class=\"sequence-diagram\">", prefix)
                .appendLine("%s    %s", prefix, this.svg(this.fileContent(overallSvgSequenceFile(this.ramlModel, this.toDirectory))))
                .appendLine("%s  </section>", prefix)
                .appendLine("%s</article>", prefix);
    }

    private void appendResourceArticles(List<Resource> resources, FormattedWriter html, String prefix) throws IOException {
        for (Resource resource : resources) {
            this.appendResourceArticle(resource, html, prefix);
            this.appendResourceArticles(resource.resources(), html, prefix);
        }
    }

    private void appendResourceArticle(Resource resource, FormattedWriter html, String prefix) throws IOException {
        html
                .appendLine("%s<article class=\"resource\" id=\"%s\">", prefix, camelCased(resource.displayName().value())+ "-resource")
                .appendLine("%s  <header>", prefix)
                .appendLine("%s    <h1>%s</h1>", prefix, resource.displayName().value())
                .appendLine("%s  </header>", prefix)
                ;

        if(resource.description() != null) {
            html
                    .appendLine("%s  <section class=\"documentation\">", prefix)
                    .appendLine("%s  <p>%s</p>", prefix, markdownToHtml(resource.description().value()))
                    .appendLine("%s  </section>", prefix)
            ;
        }
        html
                .appendLine("%s  <section class=\"sequence-diagram\">", prefix)
                .appendLine("%s    %s", prefix, this.svg(this.fileContent(resourceSvgSequenceFile(this.ramlModel, resource, this.toDirectory))))
                .appendLine("%s  </section>", prefix);

        if(! resource.methods().isEmpty()) {
            html.appendLine("%s  <section class=\"methods\">", prefix);
            for (Method method : resource.methods()) {
                this.appendDocumentation(method, html, prefix + "    ");
            }
            html.appendLine("%s  </section>", prefix);
        }

        html
                .appendLine("%s</article>", prefix);
    }

    private void appendDocumentation(Method method, FormattedWriter html, String prefix) throws IOException {
        html
                .appendLine("%s<article class=\"method\" id=\"%s-%s-method\">",
                        prefix,
                        camelCased(method.resource().displayName().value()),
                        method.method().toLowerCase()
                )
                .appendLine("%s  <h2>%s</h2>", prefix, method.method().toUpperCase())
                .appendLine("%s  <section class=\"documentation\">", prefix)
                ;



        html
                .appendLine("%s  </section>", prefix)
                .appendLine("%s</article>", prefix)
                ;
    }



    private void appendTypesArticle(FormattedWriter html, String prefix) throws IOException {
        html
                .appendLine("%s<article class=\"types\" id=\"api-types\">", prefix)
                .appendLine("%s<h1>API Types</h1>", prefix)
                ;

        for (TypeDeclaration type : this.ramlModel.getApiV10().types()) {
            html
                    .appendLine("%s<article class=\"type\" id=\"%s-type\">", prefix, type.name())
                    .appendLine("%s  <h2>%s</h2>", prefix, type.name())
                    .appendLine("%s  <section class=\"class-diaggram\">%s</section>",
                            prefix, this.svg(this.fileContent(typesSvgClassFile(ramlModel, type, toDirectory))))
                    .appendLine("%s</article>", prefix)
                    ;
        }


        html
                .appendLine("%s<article class=\"type\" id=\"all-types\">", prefix)
                .appendLine("%s  <h2>All Types</h2>", prefix)
                .appendLine("%s  <section class=\"class-diaggram\">%s</section>",
                        prefix, this.svg(this.fileContent(typesSvgClassFile(ramlModel, toDirectory))))
                .appendLine("%s</article>", prefix)
                ;
    }

    private String svg(String content) {
        int start = content.indexOf("<svg");
        if(start != -1) {
            return content.substring(start);
        } else {
            return "";
        }
    }

    private String fileContent(File file) throws IOException {
        try(Reader reader = new FileReader(file)) {
            StringBuilder result = new StringBuilder();
            char[] buffer = new char[1024];
            for(int read = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                result.append(buffer, 0, read);
            }
            return result.toString();
        }
    }
}
