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

    private final static String SRC_THEME = "https://flexiooss.github.io/fueltank-theme/api_doc_rest/";
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

                    .appendLine("    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://flexiooss.github.io/fueltank-theme/api_doc_rest/styles/ApiDocMain.css\">")
                    .appendLine("    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://flexiooss.github.io/fueltank-theme/api_doc_rest/styles/ApiDocAside.css\">")
                    .appendLine("    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://flexiooss.github.io/fueltank-theme/api_doc_rest/styles/ApiDocModal.css\">")
                    .appendLine("  </head>")
                    .appendLine("  <body>");

            html
                    .appendLine("    <header>")
                    .appendLine("      <h1>%s</h1>", ramlModel.getApiV10().title().value())
                    .appendLine("    </header>")
            ;
            this.appendNavigation(html, "    ");


            html
                    .appendLine("    <main>")
            ;

            this.appendResourceArticles(this.ramlModel.getApiV10().resources(), html, "      ");
            this.appendTypesArticle(html, "      ");
            this.appendOverviewArticle(html, "      ");

            html
                    .appendLine("    </main>")
                    .appendLine("    <div id=\"modal\">")
                    .appendLine("      <div id=\"modal-content\">")
                    .appendLine("        <span class=\"close\">&times;</span>")
                    .appendLine("        <div id=\"collage\"></div>")
                    .appendLine("      </div>")
                    .appendLine("    </div>")
                    .appendLine("  </body>")
                    .appendLine("  <script src=\"" + SRC_THEME + "js/linkManager.js\"></script>")
                    .appendLine("  </body>")
                    .appendLine("</html>")
            ;
        }
    }

    private void appendNavigation(FormattedWriter html, String prefix) throws IOException {
        html
                    .appendLine("%s<aside>", prefix)
                    .appendLine("%s  <nav>", prefix)
                    .appendLine("%s    <ul>", prefix)
                    .appendLine("%s      <li>", prefix)
                    .appendLine("%s        <span class=\"nav-title\">Ressources</span>", prefix)
                    ;

        this.appendResourceNav(this.ramlModel.getApiV10().resources(), html, prefix+ "          ");
        html
                    .appendLine("%s      </li>", prefix)
                    ;

        if(! this.ramlModel.getApiV10().types().isEmpty()) {
            html
                    .appendLine("%s      <li>", prefix)
                    .appendLine("%s        <span class=\"nav-title\">", prefix)
                    .appendLine("%s          <a href=\"#api-types\">API Types</a>", prefix)
                    .appendLine("%s        </span>", prefix)
                    .appendLine("%s        <nav>", prefix)
                    .appendLine("%s          <ul>", prefix)
                    ;
            for (TypeDeclaration type : this.ramlModel.getApiV10().types()) {
                html.appendLine("%s            <li><a href=\"#%s-type\">%s</a></li>", prefix, type.name(), type.name());
            }
            html    .appendLine("%s            <li><a href=\"#all-types\">All types</a></li>", prefix);
            html
                    .appendLine("%s          </ul>", prefix)
                    .appendLine("%s        </nav>", prefix)
                    .appendLine("%s      </li>", prefix);
        }

        html
                    .appendLine("%s      <li>", prefix)
                    .appendLine("%s        <span class=\"nav-title\"><a href=\"#overview\">Overview</a></span>", prefix)
                    .appendLine("%s      </li>", prefix)
                    .appendLine("%s    </ul>", prefix)
                    .appendLine("%s  </nav>", prefix)
                    .appendLine("%s</aside>", prefix)
                    ;
    }

    private void appendResourceNav(List<Resource> resources, FormattedWriter html, String prefix) throws IOException {
        if(resources.isEmpty()) return;

        html
                    .appendLine("%s<nav>", prefix)
                    .appendLine("%s  <ul>", prefix)
                    ;

        for (Resource resource : resources) {
            this.appendRessourceItems(html, prefix, resource);
        }


        html
                    .appendLine("%s  </ul>", prefix)
                    .appendLine("%s</nav>", prefix)
        ;
    }

    private void appendRessourceItems(FormattedWriter html, String prefix, Resource resource) throws IOException {
        html
                    .appendLine("%s    <li>", prefix)
                    .appendLine("%s      <a href=\"#%s\">%s</a>", prefix, camelCased(resource.displayName().value()) + "-resource", this.resourceUrl(resource))
                    .appendLine("%s    </li>", prefix);
        for (Resource sub : resource.resources()) {
            this.appendRessourceItems(html, prefix, sub);
        }

    }

    private void appendOverviewArticle(FormattedWriter html, String prefix) throws IOException {
        html
                    .appendLine("%s<article id=\"%s\">", prefix, "overview")
                    .appendLine("%s  <header>", prefix)
                    .appendLine("%s    <h2>Overview</h2>", prefix)
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
                    .appendLine("%s    <h2>%s</h2>", prefix, this.resourceUrl(resource))
                    .appendLine("%s    <ressource-name>%s</ressource-name>", prefix, resource.displayName().value())
                    .appendLine("%s  </header>", prefix)
                ;
        if(resource.description() != null) {
            html
                    .appendLine("%s  <section class=\"documentation\">", prefix)
                    .appendLine("%s  <div class=\"description\">", prefix)
                    .appendLine("%s     %s", prefix, markdownToHtml(resource.description().value()))
                    .appendLine("%s  </div>", prefix)
                    .appendLine("%s  </section>", prefix)
            ;
        }

        if(! resource.methods().isEmpty()) {
            html    .appendLine("%s  <section class=\"methods\">", prefix);
            for (Method method : resource.methods()) {
                this.appendDocumentation(method, html, prefix + "    ");
            }
            html    .appendLine("%s  </section>", prefix);
        }

        html
                .appendLine("%s  <section class=\"sequence-diagram\">", prefix)
                .appendLine("%s    %s", prefix, this.svg(this.fileContent(resourceSvgSequenceFile(this.ramlModel, resource, this.toDirectory))))
                .appendLine("%s  </section>", prefix);

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
                    .appendLine("%s  <label for=\"%s\">",
                            prefix,
                            camelCased(method.resource().displayName().value()) + "-display-doc" + method.method().toLowerCase())
                    .appendLine("%s    <h3>%s %s</h3>",
                            prefix,
                            method.method().toLowerCase(),
                            this.resourceUrl(method.resource()))
                    .appendLine("%s    <span class=\"%s\">%s</span>",
                            prefix,
                            method.method().toLowerCase(),
                            method.method().toUpperCase())
                    .appendLine("%s  </label>", prefix)
                    .appendLine("%s  <input type=\"checkbox\" id=\"%s\">", prefix, camelCased(method.resource().displayName().value()) + "-display-doc" + method.method().toLowerCase())
                    .appendLine("%s  <section class=\"documentation\">", prefix)
                    ;


        List<TypeDeclaration> uriParameters = this.resolvedUriParameters(method.resource());
        if(! uriParameters.isEmpty()) {
            html.appendLine("%s    <section class=\"uri-parameters\">", prefix);
            html.appendLine("%s      <h4>uri parameters</h4>", prefix);
            this.parameterSections(uriParameters, html, prefix + "      ", "uri-parameter", "h5");
            html.appendLine("%s    </section>", prefix);
        }

        if(! method.queryParameters().isEmpty()) {
            html.appendLine("%s    <section class=\"query-parameters\">", prefix);
            html.appendLine("%s      <h4>query parameters</h4>", prefix);
            this.parameterSections(method.queryParameters(), html, prefix + "      ", "query-parameter", "h5");
            html.appendLine("%s    </section>", prefix);
        }

        if(! method.headers().isEmpty()) {
            html.appendLine("%s    <section class=\"headers\">", prefix);
            html.appendLine("%s      <h4>headers</h4>", prefix);
            this.parameterSections(method.headers(), html, prefix + "      ", "header", "h5");
            html.appendLine("%s    </section>", prefix);
        }

        if(! method.body().isEmpty()) {
            html.appendLine("%s    <section class=\"request-payload-parts\">", prefix);
            html.appendLine("%s      <h4>request payload</h4>", prefix);
            this.parameterSections(method.body(), html, prefix + "      ", "request-payload-part", "h5");
            html.appendLine("%s    </section>", prefix);
        }

        if(method.responses() != null && ! method.responses().isEmpty()) {
            html.appendLine("%s    <section class=\"responses\">", prefix);
            html.appendLine("%s      <h4>responses</h4>", prefix);
            for (Response response : method.responses()) {
                html.appendLine("%s      <section class=\"response\">", prefix);
                html.appendLine("%s        <h5>%s</h5>", prefix, response.code().value());
                if(response.description() != null) {
                    html.appendLine("%s        <div class=\"description\">", prefix);
                    html.appendLine("%s          %s%s        </div>", prefix, markdownToHtml(response.description().value()), prefix);
                }


                if(! response.headers().isEmpty()) {
                    html.appendLine("%s        <section class=\"headers\">", prefix);
                    html.appendLine("%s        <h6>headers</h6>", prefix);
                    this.parameterSections(response.headers(), html, prefix + "        ", "header", "h7");
                    html.appendLine("%s        </section>", prefix);
                }
                if(! response.body().isEmpty()) {
                    html.appendLine("%s        <section class=\"response-payload-part\">", prefix);
                    html.appendLine("%s        <h6>response payload</h6>", prefix);
                    this.parameterSections(response.body(), html, prefix + "        ", "response-payload-part", "h7");
                    html.appendLine("%s        </section>", prefix);
                }


                html.appendLine("%s      </section>", prefix);
            }
            html.appendLine("%s    </section>", prefix);
        }

        html
                .appendLine("%s  </section>", prefix)
                .appendLine("%s</article>", prefix)
                ;
    }

    private String resourceUrl(Resource resource) {
        String path = resource.resourcePath();
        List<TypeDeclaration> uriParameters = new LinkedList<>();
        Resource r = resource;
        while (r != null) {
            uriParameters.addAll(r.uriParameters());
            r = r.parentResource();
        }

        if (!uriParameters.isEmpty()) {
            for (TypeDeclaration uriParam : uriParameters) {
                path = path.replaceAll("\\{" + uriParam.name() + "\\}", "<span class=\"uri-param\">" + uriParam.name() + "</span>");
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
                .appendLine("%s  <header>", prefix)
                .appendLine("%s<h2>API Types</h2>", prefix)
                .appendLine("%s  </header>", prefix)
                ;

        String parentPrefix = prefix;
        prefix += "  ";

        for (TypeDeclaration type : this.ramlModel.getApiV10().types()) {
            html
                    .appendLine("%s<article class=\"type\" id=\"%s-type\">", prefix, type.name())
                    .appendLine("%s  <h3>%s</h3>", prefix, type.name())
                    ;
            if(typesSvgClassFile(ramlModel, type, toDirectory).exists()) {
                html
                        .appendLine("%s  <section class=\"class-diaggram\">%s</section>",
                                prefix, this.svg(this.fileContent(typesSvgClassFile(ramlModel, type, toDirectory))))
                ;
            }
            html.appendLine("%s</article>", prefix);
        }


        html
                .appendLine("%s<article class=\"type\" id=\"all-types\">", prefix)
                .appendLine("%s  <h3>All Types</h3>", prefix)
                .appendLine("%s  <section class=\"class-diaggram\">%s</section>",
                        prefix, this.svg(this.fileContent(typesSvgClassFile(ramlModel, toDirectory))))
                .appendLine("%s</article>", prefix)
                ;

        prefix = parentPrefix;
        html.appendLine("%s</article>", prefix);
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
            System.out.println("IMPORTING FILE CONTENT : " + file.getAbsolutePath() + "\n" + result.toString() + "#######\n\n");
            return result.toString();
        }
    }
}
