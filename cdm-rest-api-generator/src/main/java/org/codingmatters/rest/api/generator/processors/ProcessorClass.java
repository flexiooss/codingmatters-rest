package org.codingmatters.rest.api.generator.processors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.squareup.javapoet.*;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.api.generator.handlers.HandlersHelper;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nelt on 5/23/17.
 */
public class ProcessorClass {

    static private final Logger log = LoggerFactory.getLogger(ProcessorClass.class);

    private final String typesPackage;
    private final String apiPackage;
    private final Naming naming;
    private final HandlersHelper helper;

    public ProcessorClass(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
    }

    public TypeSpec type(RamlModelResult ramlModel) {
        String processorTypeName = this.naming.type(ramlModel.getApiV10().title().value(), "Processor");
        return TypeSpec.classBuilder(processorTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(Processor.class))
                .addMethod(this.buildProcessMethod(ramlModel))
                .addMethods(this.buidMethodProcessingMethods(ramlModel))
                .addField(FieldSpec.builder(Logger.class, "log", Modifier.STATIC, Modifier.PRIVATE)
                        .initializer("$T.getLogger($T.class)", LoggerFactory.class, ClassName.bestGuess(processorTypeName))
                        .build())
                .addField(ClassName.get(String.class), "apiPath", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.get(JsonFactory.class), "factory", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Handlers")), "handlers", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(String.class), "apiPath")
                        .addParameter(ClassName.get(JsonFactory.class), "factory")
                        .addParameter(ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Handlers")), "handlers")
                        .addStatement("this.$L = $L", "apiPath", "apiPath")
                        .addStatement("this.$L = $L", "factory", "factory")
                        .addStatement("this.$L = $L", "handlers", "handlers")
                        .build())
                .build();
    }

    private MethodSpec buildProcessMethod(RamlModelResult ramlModel) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("process")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(RequestDelegate.class), "requestDelegate")
                .addParameter(ClassName.get(ResponseDelegate.class), "responseDelegate")
                .addException(ClassName.get(IOException.class));

        method.addStatement("responseDelegate.contenType($S)", "application/json; charset=utf-8");
        this.addResourcesProcessing( method, ramlModel.getApiV10().resources());

        return method.build();
    }

    private void addResourcesProcessing(MethodSpec.Builder method, List<Resource> resources) {
        for (Resource resource : resources) {
            this.addResourceProcessing(method, resource);
            this.addResourcesProcessing(method, resource.resources());
        }
    }

    private void addResourceProcessing(MethodSpec.Builder method, Resource resource) {
        if(resource.methods().isEmpty()) return;

        if(! resource.uriParameters().isEmpty()) {
            String pathRegex = resource.resourcePath().replaceAll("\\{[^\\}]*}", "[^/]+");
            method.beginControlFlow("if(requestDelegate.pathMatcher(this.apiPath + \"$L/?\").matches())", pathRegex);
        } else {
            method.beginControlFlow(
                    "if(requestDelegate.pathMatcher(this.apiPath + \"$L/?\").matches())",
                    resource.resourcePath()
            );
        }
        for (Method resourceMethod : resource.methods()) {
            method.beginControlFlow(
                    "if(requestDelegate.method().equals(RequestDelegate.Method.$L))",
                    resourceMethod.method().toUpperCase()
            );
            method.addStatement("this.$L(requestDelegate, responseDelegate)", this.methodProcessingMethodName(resourceMethod));
            method.endControlFlow();
        }

        method.endControlFlow();

    }



    private Iterable<MethodSpec> buidMethodProcessingMethods(RamlModelResult ramlModel) {
        LinkedList<MethodSpec> methodSpecs = new LinkedList<>();
        this.addMethodProcessingMethods(methodSpecs, ramlModel.getApiV10().resources());
        return methodSpecs;
    }

    private void addMethodProcessingMethods(Collection<MethodSpec> methodSpecs, List<Resource> resources) {
        for (Resource resource : resources) {
            for (Method method : resource.methods()) {
                methodSpecs.add(this.buildMethodProcessingMethod(method));
            }
            this.addMethodProcessingMethods(methodSpecs, resource.resources());
        }
    }

    private MethodSpec buildMethodProcessingMethod(Method resourceMethod) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(this.methodProcessingMethodName(resourceMethod))
                .addModifiers(Modifier.PRIVATE)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(RequestDelegate.class), "requestDelegate")
                .addParameter(ClassName.get(ResponseDelegate.class), "responseDelegate")
                .addException(IOException.class);

        this.addMethodProcessingMethodBody(resourceMethod, method);

        return method.build();
    }

    private void addMethodProcessingMethodBody(Method resourceMethod, MethodSpec.Builder method) {
        method.addStatement("$T.Builder requestBuilder = $T.Builder.builder()",
                this.resourceMethodRequestClass(resourceMethod),
                this.resourceMethodRequestClass(resourceMethod)
        );
        if(! resourceMethod.body().isEmpty()) {
            this.addRequestPayloadProcessing(resourceMethod, method);
        }
        if(! resourceMethod.queryParameters().isEmpty()) {
            this.addRequestQueryParametersProcessing(resourceMethod, method);
        }
        if(! resourceMethod.resource().uriParameters().isEmpty()) {
            this.addRequestUriParametersProcessing(resourceMethod, method);
        }
        if(! resourceMethod.headers().isEmpty()) {
            this.addRequestHeadersProcessing(resourceMethod, method);
        }
        method
                .addStatement(
                        "$T response = this.handlers.$L().apply(requestBuilder.build())",
                        this.resourceMethodResponseClass(resourceMethod),
                        this.resourceMethodHandlerMethod(resourceMethod)
                );

        this.addResponseProcessingStatements(resourceMethod, method);

    }

    private void addRequestPayloadProcessing(Method resourceMethod, MethodSpec.Builder method) {
        method.addStatement("$T payload = requestDelegate.payload()", InputStream.class);
        method.beginControlFlow("try");
        method.addStatement("$T parser = this.factory.createParser(payload)", JsonParser.class);
        method.addStatement("requestBuilder.payload(new $T().read(parser))",
                ClassName.get(this.typesPackage + ".json", this.naming.type(resourceMethod.body().get(0).type(), "Reader"))
        );
        method.nextControlFlow("catch(IOException e)");
        method
                .addStatement("responseDelegate.status($L).payload($S, $S)", 400, "bad request body, see logs", "utf-8")
                .addStatement("log.info($S)", "malformed request")
                .addStatement("return");

        method.endControlFlow();
    }

    private void addRequestHeadersProcessing(Method resourceMethod, MethodSpec.Builder method) {
        for (TypeDeclaration typeDeclaration : resourceMethod.headers()) {
            if(typeDeclaration.type().equalsIgnoreCase("string")) {
                method
                        .addStatement(
                                "$T $L = requestDelegate.headers().get($S) != null " +
                                        "&& ! requestDelegate.headers().get($S).isEmpty() ? " +
                                        "requestDelegate.headers().get($S).get(0) : null",
                                String.class, typeDeclaration.name(), typeDeclaration.name(),
                                typeDeclaration.name(),
                                typeDeclaration.name()
                        )
                        .addStatement(
                                "requestBuilder.$L($L)", typeDeclaration.name(), typeDeclaration.name()
                        );
            } else if(typeDeclaration.type().equalsIgnoreCase("array")
                    && ((ArrayTypeDeclaration)typeDeclaration).items().type().equalsIgnoreCase("string")) {
                method
                        .addStatement(
                                "$T<$T> $L = requestDelegate.headers().get($S)",
                                List.class, String.class, typeDeclaration.name(), typeDeclaration.name()
                        )
                        .addStatement(
                                "requestBuilder.$L($L)", typeDeclaration.name(), typeDeclaration.name()
                        );
            } else {
                log.warn("not yet implemented : {} query parameter", typeDeclaration);
            }
        }
    }

    private void addRequestQueryParametersProcessing(Method resourceMethod, MethodSpec.Builder method) {
        for (TypeDeclaration typeDeclaration : resourceMethod.queryParameters()) {
            if(typeDeclaration.type().equalsIgnoreCase("string")) {
                method
                        .addStatement(
                                "$T $L = requestDelegate.queryParameters().get($S) != null " +
                                        "&& ! requestDelegate.queryParameters().get($S).isEmpty() ? " +
                                        "requestDelegate.queryParameters().get($S).get(0) : null",
                                String.class, typeDeclaration.name(), typeDeclaration.name(),
                                typeDeclaration.name(),
                                typeDeclaration.name()
                        )
                        .addStatement(
                                "requestBuilder.$L($L)", typeDeclaration.name(), typeDeclaration.name()
                        );
            } else if(typeDeclaration.type().equalsIgnoreCase("array")
                    && ((ArrayTypeDeclaration)typeDeclaration).items().type().equalsIgnoreCase("string")) {
                method
                        .addStatement(
                                "$T<$T> $L = requestDelegate.queryParameters().get($S)",
                                List.class, String.class, typeDeclaration.name(), typeDeclaration.name()
                        )
                        .addStatement(
                                "requestBuilder.$L($L)", typeDeclaration.name(), typeDeclaration.name()
                        );
            } else {
                log.warn("not yet implemented : {} query parameter", typeDeclaration);
            }
        }
    }

    private void addRequestUriParametersProcessing(Method resourceMethod, MethodSpec.Builder method) {
        method.addStatement(
                "$T<$T, $T<$T>> uriParameters = requestDelegate.uriParameters(this.apiPath + \"$L/?\")",
                Map.class, String.class, List.class, String.class, resourceMethod.resource().resourcePath()
            );
        for (TypeDeclaration typeDeclaration : resourceMethod.resource().uriParameters()) {
            if(typeDeclaration.type().equalsIgnoreCase("string")) {
                method
                        .addStatement(
                                "$T $L = uriParameters.get($S) != null " +
                                        "&& ! uriParameters.get($S).isEmpty() ? " +
                                        "uriParameters.get($S).get(0) : null",
                                String.class, typeDeclaration.name(), typeDeclaration.name(),
                                typeDeclaration.name(),
                                typeDeclaration.name()
                        )
                        .addStatement(
                                "requestBuilder.$L($L)", typeDeclaration.name(), typeDeclaration.name()
                        );
            } else if(typeDeclaration.type().equalsIgnoreCase("array")
                    && ((ArrayTypeDeclaration)typeDeclaration).items().type().equalsIgnoreCase("string")) {
                method
                        .addStatement(
                                "$T<$T> $L = uriParameters.get($S)",
                                List.class, String.class, typeDeclaration.name(), typeDeclaration.name()
                        )
                        .addStatement(
                                "requestBuilder.$L($L)", typeDeclaration.name(), typeDeclaration.name()
                        );
            } else {
                log.warn("not yet implemented : {} uri parameter", typeDeclaration);
            }
        }
    }

    private void addResponseProcessingStatements(Method resourceMethod, MethodSpec.Builder method) {
        method.beginControlFlow("if(response != null)");
        if(! resourceMethod.responses().isEmpty()) {
            for (int i = 0; i < resourceMethod.responses().size(); i++) {
                Response response = resourceMethod.responses().get(i);
                if (i == 0) {
                    method.beginControlFlow("if (response.status$L() != null)", response.code().value());
                } else {
                    method.nextControlFlow("else if (response.status$L() != null)", response.code().value());
                }
                method.addStatement("responseDelegate.status($L)", response.code().value());

                if (!response.headers().isEmpty()) {
                    this.addResponseHeadersProcessingStatements(response, method);
                }
                if (!response.body().isEmpty()) {
                    this.addResponsePayloadProcessingStatements(response, method);
                }
            }
            method.endControlFlow();
        }
        method.endControlFlow();
    }

    private void addResponseHeadersProcessingStatements(Response response, MethodSpec.Builder method) {
        for (TypeDeclaration typeDeclaration : response.headers()) {
            method.beginControlFlow(
                    "if(response.status$L().$L() != null)",
                    response.code().value(),
                    typeDeclaration.name()
            );
            if(typeDeclaration.type().equalsIgnoreCase("string")) {
                method.addStatement(
                        "responseDelegate.addHeader($S, response.status$L().$L())",
                        typeDeclaration.name(),
                        response.code().value(),
                        typeDeclaration.name()
                );
            } else if(typeDeclaration.type().equalsIgnoreCase("array")
                    && ((ArrayTypeDeclaration)typeDeclaration).items().type().equalsIgnoreCase("string")) {
                method.beginControlFlow(
                        "for($T element: response.status$L().$L())",
                        String.class,
                        response.code().value(),
                        typeDeclaration.name()
                );
                method.addStatement("responseDelegate.addHeader($S, element)", typeDeclaration.name());
                method.endControlFlow();
            } else {
                log.warn("not yet implemented : {} response header type", typeDeclaration);
            }
            method.endControlFlow();
        }
    }

    private void addResponsePayloadProcessingStatements(Response response, MethodSpec.Builder method) {
        TypeDeclaration body = response.body().get(0);
        method.beginControlFlow("if(response.status$L().payload() != null)", response.code().value());
        method.beginControlFlow("try($T out = new $T())", ByteArrayOutputStream.class, ByteArrayOutputStream.class);
        method.beginControlFlow("try($T generator = this.factory.createGenerator(out))", JsonGenerator.class);
        method.addStatement(
                "new $T().write(generator, response.status$L().payload())",
                ClassName.get(this.typesPackage + ".json", this.naming.type(body.type(), "Writer")),
                response.code().value()
        );
        method.endControlFlow();
        method.addStatement("responseDelegate.payload(out.toString(), $S)", "utf-8");
        method.endControlFlow();
        method.endControlFlow();
    }

    private String resourceMethodHandlerMethod(Method resourceMethod) {
        return this.naming.property(resourceMethod.resource().displayName().value(), resourceMethod.method(), "Handler");
    }

    private ClassName resourceMethodRequestClass(Method resourceMethod) {
        return ClassName.get(this.apiPackage, this.naming.type(resourceMethod.resource().displayName().value(), resourceMethod.method(), "Request"));
    }

    private ClassName resourceMethodResponseClass(Method resourceMethod) {
        return ClassName.get(this.apiPackage, this.naming.type(resourceMethod.resource().displayName().value(), resourceMethod.method(), "Response"));
    }

    private String methodProcessingMethodName(Method method) {
        return this.naming.property("process", method.resource().displayName().value(), method.method(), "Request");
    }
}
