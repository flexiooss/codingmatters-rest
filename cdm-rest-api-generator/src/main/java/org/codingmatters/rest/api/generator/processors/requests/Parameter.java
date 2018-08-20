package org.codingmatters.rest.api.generator.processors.requests;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Parameter {
    static private final Logger log = LoggerFactory.getLogger(Parameter.class);

    private final Naming naming;
    private final TypeDeclaration typeDeclaration;

    public Parameter(Naming naming, TypeDeclaration typeDeclaration) {
        this.naming = naming;
        this.typeDeclaration = typeDeclaration;
    }

    public void addQueryParameterStatement(MethodSpec.Builder method) {
        this.addStatement(method, ParameterSource.QUERY);
    }

    public void addHeaderStatement(MethodSpec.Builder method) {
        this.addStatement(method, ParameterSource.HEADERS);
    }

    public void addUriParametersStatement(MethodSpec.Builder method) {
        this.addStatement(method, ParameterSource.URI);
    }

    private  void addStatement(MethodSpec.Builder method, ParameterSource source) {
        if(this.isSupportedType()) {
            if(! this.isArray()) {
                this.addRawGetStatement(method, source);
                this.addTranstypeStatement(method, this.property(), this.property() + "RawValue");
                method
                        .addStatement(
                                "requestBuilder.$L($L)", this.property(), this.property()
                        );
            } else {
                this.addRawArrayGetStatement(method, source);
                method.addStatement("$T $L = null", List.class, this.property());
                method.beginControlFlow("if($L != null)", this.property() + "RawValue")
                        .addStatement("$L = new $T()", this.property(), LinkedList.class)
                        .beginControlFlow("for($T rawElement : $L)", String.class, this.property() + "RawValue");
                this.addTranstypeStatement(method, "element", "rawElement");
                method.addStatement("$L.add(element)", this.property());
                method.endControlFlow()
                        .endControlFlow();

                method
                        .addStatement(
                                "requestBuilder.$L($L)", this.property(), this.property()
                        );
            }
        } else {
            log.error("not yet implemented parameter : name={} type={}", typeDeclaration.name(), typeDeclaration.type());
        }
    }

    private void addRawGetStatement(MethodSpec.Builder method, ParameterSource source) {
        switch (source) {
            case HEADERS:
            case QUERY:
                method
                        .addStatement(
                                "$T $L = requestDelegate." + source.delegateMethod + "().get($S) != null && " +
                                        "! requestDelegate." + source.delegateMethod + "().get($S).isEmpty() ? " +
                                        "requestDelegate." + source.delegateMethod + "().get($S).get(0) : " +
                                        "null",
                                String.class, this.property() + "RawValue",
                                typeDeclaration.name(),
                                typeDeclaration.name(),
                                typeDeclaration.name()
                        );
                break;
            case URI:
                method
                        .addStatement(
                                "$T $L = uriParameters.get($S) != null " +
                                        "&& ! uriParameters.get($S).isEmpty() ? " +
                                        "uriParameters.get($S).get(0) : null",
                                String.class, this.property() + "RawValue",
                                typeDeclaration.name(),
                                typeDeclaration.name(),
                                typeDeclaration.name()
                        );
                break;
        }
    }

    private void addRawArrayGetStatement(MethodSpec.Builder method, ParameterSource source) {
        switch (source) {
            case HEADERS:
            case QUERY:
                method.addStatement(
                        "$T<$T> $L = requestDelegate." + source.delegateMethod + "().get($S)",
                        List.class, String.class, this.property() + "RawValue",
                        typeDeclaration.name()
                );
                break;
            case URI:
                method.addStatement(
                        "$T<$T> $L = uriParameters.get($S)",
                        List.class, String.class, this.property() + "RawValue",
                        typeDeclaration.name()
                );
                break;
        }
    }


    private void addTranstypeStatement(MethodSpec.Builder method, String varname, String rawVarName) {
        if(this.isOfType("string")) {
            method.addStatement("$T $L = $L", String.class, varname, rawVarName);
        } else if(this.isOfType("integer")) {
            method.addStatement("$T $L = $L != null ? $T.parseLong($L) : null", Long.class, varname, rawVarName, Long.class, rawVarName);
        } else if(this.isOfType("number")) {
            method.addStatement("$T $L = $L != null ? $T.parseDouble($L) : null", Double.class, varname, rawVarName, Double.class, rawVarName);
        } else if(this.isOfType("datetime-only")) {
            method.addStatement("$T $L = $L != null ? $T.parse($L) : null",
                    LocalDateTime.class, varname, rawVarName, LocalDateTime.class, rawVarName
            );
        } else if(this.isOfType("date-only")) {
            method.addStatement("$T $L = $L != null ? $T.parse($L) : null",
                    LocalDate.class, varname, rawVarName, LocalDate.class, rawVarName
            );
        } else if(this.isOfType("time-only")) {
            method.addStatement("$T $L = $L != null ? $T.parse($L) : null",
                    LocalTime.class, varname, rawVarName, LocalTime.class, rawVarName
            );
        } else if(this.isOfType("boolean")) {
            method.addStatement("$T $L = $L != null ? ($S.equals($L) || $S.equals($L) ? $T.TRUE : $T.FALSE) : null",
                    Boolean.class, varname, rawVarName, "true", rawVarName, "1", rawVarName, Boolean.class, Boolean.class
                    );
        }
    }

    static private Set<String> SUPPPORTED_RAML_TYPES = new HashSet<String>() {
        {
            add("string");
            add("integer");
            add("number");
            add("datetime-only");
            add("date-only");
            add("time-only");
            add("boolean");
        }
    };

    private boolean isSupportedType() {
        return SUPPPORTED_RAML_TYPES.contains(this.ramlType().toLowerCase());
    }

    private String ramlType() {
        String t;
        if(this.isArray()) {
            t = ((ArrayTypeDeclaration)typeDeclaration).items().type().toLowerCase();
        } else {
            t = this.typeDeclaration.type();
        }
        return t;
    }

    private String property() {
        return this.naming.property(typeDeclaration.name());
    }

    private boolean isOfType(String ramlTypeName) {
        return this.ramlType().equalsIgnoreCase(ramlTypeName);
    }

    private boolean isArray() {
        return this.typeDeclaration.type().equalsIgnoreCase("array");
    }

    enum ParameterSource {
        HEADERS("headers"), QUERY("queryParameters"), URI("uriParameters");

        public final String delegateMethod;

        ParameterSource(String delegateMethod) {
            this.delegateMethod = delegateMethod;
        }
    }
}
