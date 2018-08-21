package org.codingmatters.rest.api.generator.processors;

import com.fasterxml.jackson.core.JsonFactory;
import com.squareup.javapoet.*;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;
import org.codingmatters.rest.api.generator.exception.UnsupportedMediaTypeException;
import org.codingmatters.rest.api.generator.handlers.HandlersHelper;
import org.codingmatters.rest.api.generator.processors.requests.ProcessorParameter;
import org.codingmatters.rest.api.generator.type.SupportedMediaType;
import org.codingmatters.rest.api.generator.utils.DeclaredTypeRegistry;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.codingmatters.rest.api.generator.utils.Resolver;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
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

    private final ProcessorResponse processorResponse;

    public ProcessorClass(String typesPackage, String apiPackage, Naming naming, HandlersHelper helper) {
        this.typesPackage = typesPackage;
        this.apiPackage = apiPackage;
        this.naming = naming;
        this.helper = helper;
        this.processorResponse = new ProcessorResponse(this.typesPackage, this.naming);
    }

    public TypeSpec type(RamlModelResult ramlModel) {
        DeclaredTypeRegistry.initialize(ramlModel);

        TypeSpec.Builder processorBuilder = TypeSpec.classBuilder(this.naming.type(ramlModel.getApiV10().title().value(), "Processor"))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(Processor.class))
                .addMethod(this.buildProcessMethod(ramlModel))
                .addMethods(this.buildMethodProcessingMethods(ramlModel))
                .addField(FieldSpec.builder(Logger.class, "log", Modifier.STATIC, Modifier.PRIVATE)
                        .initializer("$T.getLogger($T.class)", LoggerFactory.class, this.processorClassName(ramlModel))
                        .build())
                .addField(ClassName.get(String.class), "apiPath", Modifier.PRIVATE, Modifier.FINAL)
                .addField(ClassName.get(JsonFactory.class), "factory", Modifier.PRIVATE, Modifier.FINAL)
                .addField(this.handlersClassName(ramlModel), "handlers", Modifier.PRIVATE, Modifier.FINAL)


                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ClassName.get(String.class), "apiPath")
                        .addParameter(ClassName.get(JsonFactory.class), "factory")
                        .addParameter(this.handlersClassName(ramlModel), "handlers")
                        .addStatement("this.$L = $L", "apiPath", "apiPath")
                        .addStatement("this.$L = $L", "factory", "factory")
                        .addStatement("this.$L = $L", "handlers", "handlers")
                        .build());
        if(this.processorResponse.needsSubstitutedMethod()) {
            processorBuilder.addMethod(this.buildSubstitutedMethod());
        }
        return processorBuilder
                .build();
    }

    private ClassName handlersClassName(RamlModelResult ramlModel) {
        return ClassName.get(this.apiPackage, this.naming.type(ramlModel.getApiV10().title().value(), "Handlers"));
    }

    private ClassName processorClassName(RamlModelResult ramlModel) {
        return ClassName.bestGuess(this.naming.type(ramlModel.getApiV10().title().value(), "Processor"));
    }

    private MethodSpec buildProcessMethod(RamlModelResult ramlModel) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("process")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.get(RequestDelegate.class), "requestDelegate")
                .addParameter(ClassName.get(ResponseDelegate.class), "responseDelegate")
                .addException(ClassName.get(IOException.class));

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

        if(! Resolver.resolvedUriParameters(resource).isEmpty()) {
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
            method.addStatement("return");
            method.endControlFlow();
        }

        method.endControlFlow();

    }



    private Iterable<MethodSpec> buildMethodProcessingMethods(RamlModelResult ramlModel) {
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
        method.addStatement("$T.Builder requestBuilder = $T.builder()",
                this.resourceMethodRequestClass(resourceMethod),
                this.resourceMethodRequestClass(resourceMethod)
        );
        if(! resourceMethod.body().isEmpty()) {
            this.addRequestPayloadProcessing(resourceMethod, method);
        }
        if(! resourceMethod.queryParameters().isEmpty()) {
            this.addRequestQueryParametersProcessing(resourceMethod, method);
        }
        if(! Resolver.resolvedUriParameters(resourceMethod.resource()).isEmpty()) {
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
        SupportedMediaType mediaType;
        try {
            mediaType = SupportedMediaType.from(resourceMethod);
        } catch (UnsupportedMediaTypeException e) {
            log.error("error while processing response", e);
            return ;
        }

        method.beginControlFlow("try($T payload = requestDelegate.payload())", InputStream.class);
        mediaType.processorBodyReaderStatement(resourceMethod, this.typesPackage, this.naming).append(method);
        method.endControlFlow();
    }

    private void addRequestHeadersProcessing(Method resourceMethod, MethodSpec.Builder method) {
        for (TypeDeclaration typeDeclaration : resourceMethod.headers()) {
            ProcessorParameter param = new ProcessorParameter(this.naming, typeDeclaration);
            param.addStatement(method, Parameter.ParameterSource.HEADERS);
        }
    }

    private void addRequestQueryParametersProcessing(Method resourceMethod, MethodSpec.Builder method) {
        for (TypeDeclaration typeDeclaration : resourceMethod.queryParameters()) {
            ProcessorParameter param = new ProcessorParameter(this.naming, typeDeclaration);
            param.addStatement(method, Parameter.ParameterSource.QUERY);
        }
    }

    private void addRequestUriParametersProcessing(Method resourceMethod, MethodSpec.Builder method) {
        method.addStatement(
                "$T<$T, $T<$T>> uriParameters = requestDelegate.uriParameters(this.apiPath + \"$L/?\")",
                Map.class, String.class, List.class, String.class, resourceMethod.resource().resourcePath()
            );
        for (TypeDeclaration typeDeclaration : Resolver.resolvedUriParameters(resourceMethod.resource())) {
            ProcessorParameter param = new ProcessorParameter(this.naming, typeDeclaration);
            param.addStatement(method, Parameter.ParameterSource.URI);
        }

//        for (TypeDeclaration typeDeclaration : Resolver.resolvedUriParameters(resourceMethod.resource())) {
//            String property = this.naming.property(typeDeclaration.name());
//            if(typeDeclaration.type().equalsIgnoreCase("string")) {
//                method
//                        .addStatement(
//                                "$T $L = uriParameters.get($S) != null " +
//                                        "&& ! uriParameters.get($S).isEmpty() ? " +
//                                        "uriParameters.get($S).get(0) : null",
//                                String.class, property, typeDeclaration.name(),
//                                typeDeclaration.name(),
//                                typeDeclaration.name()
//                        )
//                        .addStatement(
//                                "requestBuilder.$L($L)", property, property
//                        );
//            } else if(typeDeclaration.type().equalsIgnoreCase("array")
//                    && ((ArrayTypeDeclaration)typeDeclaration).items().type().equalsIgnoreCase("string")) {
//                method
//                        .addStatement(
//                                "$T<$T> $L = uriParameters.get($S)",
//                                List.class, String.class, property, typeDeclaration.name()
//                        )
//                        .addStatement(
//                                "requestBuilder.$L($L)", property, property
//                        );
//            } else {
//                log.warn("not yet implemented : {} uri parameter", typeDeclaration);
//            }
//        }
    }

    private void addResponseProcessingStatements(Method resourceMethod, MethodSpec.Builder method) {
        this.processorResponse.append(resourceMethod, method);
    }


    private MethodSpec buildSubstitutedMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder("substituted")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(RequestDelegate.class, "requestDelegate")
                .addParameter(String.class, "str")
                .returns(String.class);

        method.beginControlFlow("if(str != null)")
                .addStatement("str = str.replaceAll($S, requestDelegate.absolutePath(this.apiPath))", "%API_PATH%")
                .endControlFlow();

        return method
                .addStatement("return str")
                .build();
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
