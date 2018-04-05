package org.codingmatters.rest.api.doc;

import org.codingmatters.value.objects.FormattedWriter;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.*;
import java.util.LinkedList;
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
                    .appendLine("%s  <div class=\"description\">%s</div>", prefix, markdownToHtml(resource.description().value()))
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
                .appendLine("%s  <h2>%s %s</h2>", prefix, method.method().toUpperCase(), this.resourceUrl(method.resource(), method))
                .appendLine("%s  <section class=\"documentation\">", prefix)
                ;


        List<TypeDeclaration> uriParameters = this.resolvedUriParameters(method.resource());
        if(! uriParameters.isEmpty()) {
            html.appendLine("%s    <section class=\"uri-parameters\">", prefix);
            html.appendLine("%s      <h3>uri parameters</h3>", prefix);
            this.parameterSections(uriParameters, html, prefix + "      ", "uri-parameter", "h4");
            html.appendLine("%s    </section>", prefix);
        }

        if(! method.queryParameters().isEmpty()) {
            html.appendLine("%s    <section class=\"query-parameters\">", prefix);
            html.appendLine("%s      <h3>query parameters</h3>", prefix);
            this.parameterSections(method.queryParameters(), html, prefix + "      ", "query-parameter", "h4");
            html.appendLine("%s    </section>", prefix);
        }

        if(! method.headers().isEmpty()) {
            html.appendLine("%s    <section class=\"headers\">", prefix);
            html.appendLine("%s      <h3>headers</h3>", prefix);
            this.parameterSections(method.headers(), html, prefix + "      ", "header", "h4");
            html.appendLine("%s    </section>", prefix);
        }

        if(! method.body().isEmpty()) {
            html.appendLine("%s    <section class=\"request-payload-parts\">", prefix);
            html.appendLine("%s      <h3>request payload</h3>", prefix);
            this.parameterSections(method.body(), html, prefix + "      ", "request-payload-part", "h4");
            html.appendLine("%s    </section>", prefix);
        }

        if(method.responses() != null && ! method.responses().isEmpty()) {
            html.appendLine("%s    <section class=\"responses\">", prefix);
            html.appendLine("%s      <h3>responses</h3>", prefix);
            for (Response response : method.responses()) {
                html.appendLine("%s      <section class=\"response\">", prefix);
                html.appendLine("%s        <h4>%s</h4>", prefix, response.code().value());
                if(response.description() != null) {
                    html.appendLine("%s        <div class=\"description\">%s</div>", prefix, markdownToHtml(response.description().value()));
                }


                if(! response.headers().isEmpty()) {
                    html.appendLine("%s        <section class=\"headers\">", prefix);
                    html.appendLine("%s        <h5>headers</h5>", prefix);
                    this.parameterSections(response.headers(), html, prefix + "        ", "header", "h6");
                    html.appendLine("%s        </section>", prefix);
                }
                if(! response.body().isEmpty()) {
                    html.appendLine("%s        <section class=\"response-payload-part\">", prefix);
                    html.appendLine("%s        <h5>response payload</h5>", prefix);
                    this.parameterSections(response.body(), html, prefix + "        ", "response-payload-part", "h6");
                    html.appendLine("%s        </section>", prefix);
                }


                html.appendLine("%s      <section>", prefix);
            }
            html.appendLine("%s    </section>", prefix);
        }

        html
                .appendLine("%s  </section>", prefix)
                .appendLine("%s</article>", prefix)
                ;
    }

    private String resourceUrl(Resource resource, Method method) {
        String path = resource.resourcePath();
        List<TypeDeclaration> uriParameters = new LinkedList<>();
        Resource r = resource;
        while (r != null) {
            uriParameters.addAll(r.uriParameters());
            r = r.parentResource();
        }

        if (!uriParameters.isEmpty()) {
            for (TypeDeclaration uriParam : uriParameters) {
                path = path.replaceAll("\\{" + uriParam.name() + "\\}", "{<b>" + uriParam.name() + "</b>}");
            }
        }

        return path;
    }

    private void parameterSections(List<TypeDeclaration> parameters, FormattedWriter html, String prefix, String elementClass, String header) throws IOException {
        for (TypeDeclaration type : parameters) {
            html.appendLine("%s<section class=\"%s\">", prefix, elementClass)
                    .appendLine("%s  <%s>%s:  %s</%s>", prefix, header, type.name(), this.formattedType(type), header)
                    ;
            if(type.description() != null) {
                html.appendLine("%s  <div class=\"description\">%s</div>", prefix, DocHelper.markdownToHtml(type.description().value()));
            }
            html.appendLine("%s</section>", prefix);
        }
    }

    private String formattedType(TypeDeclaration typeDeclaration) {
        if(typeDeclaration instanceof ArrayTypeDeclaration) {
            return this.typeName(((ArrayTypeDeclaration)typeDeclaration).items().name()) + "[]";
        } else {
            return this.typeName(typeDeclaration.type());
        }
    }

    private String typeName(String type) {
        for (TypeDeclaration declared : this.ramlModel.getApiV10().types()) {
            if(type.equals(declared.name())) {
                return String.format("<a href=\"#%s-type\">%s</a>", type, type);
            }
        }
        return type;
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

    private List<TypeDeclaration> resolvedUriParameters(Resource resource) {
        List<TypeDeclaration> result = new LinkedList<>();

        for(Resource r = resource ; r != null ; r = r.parentResource()) {
            if(r.uriParameters() != null && ! r.uriParameters().isEmpty()) {
                result.addAll(r.uriParameters());
            }
        }

        return result;
    }

    private String svg(String content) {
        int start = content.indexOf("<svg class=\"diagram\" ");
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
