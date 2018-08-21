package org.codingmatters.rest.api.generator.processors.requests;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class ProcessorParameter extends Parameter {
    static private final Logger log = LoggerFactory.getLogger(ProcessorParameter.class);

    public ProcessorParameter(Naming naming, TypeDeclaration typeDeclaration) {
        super(naming, typeDeclaration);
    }

    public void addStatement(MethodSpec.Builder method, ParameterSource source) {
        if(this.isSupportedType()) {
            if(! this.isArray()) {
                this.addRawGetStatement(method, source);
                this.addTranstypeFromStringStatement(method, this.property() + "RawValue", this.property());
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
                this.addTranstypeFromStringStatement(method, "rawElement", "element");
                method.addStatement("$L.add(element)", this.property());
                method.endControlFlow()
                        .endControlFlow();

                method
                        .addStatement(
                                "requestBuilder.$L($L)", this.property(), this.property()
                        );
            }
        } else {
            log.error("not yet implemented parameter : name={} type={}", this.name(), this.type());
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
                                this.name(),
                                this.name(),
                                this.name()
                        );
                break;
            case URI:
                method
                        .addStatement(
                                "$T $L = uriParameters.get($S) != null " +
                                        "&& ! uriParameters.get($S).isEmpty() ? " +
                                        "uriParameters.get($S).get(0) : null",
                                String.class, this.property() + "RawValue",
                                this.name(),
                                this.name(),
                                this.name()
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
                        this.name()
                );
                break;
            case URI:
                method.addStatement(
                        "$T<$T> $L = uriParameters.get($S)",
                        List.class, String.class, this.property() + "RawValue",
                        this.name()
                );
                break;
        }
    }

    protected void addTranstypeFromStringStatement(MethodSpec.Builder method, String from, String to) {
        if(this.isOfType("string")) {
            method.addStatement("$T $L = $L", String.class, to, from);
        } else if(this.isOfType("integer")) {
            method.addStatement("$T $L = $L != null ? $T.parseLong($L) : null", Long.class, to, from, Long.class, from);
        } else if(this.isOfType("number")) {
            method.addStatement("$T $L = $L != null ? $T.parseDouble($L) : null", Double.class, to, from, Double.class, from);
        } else if(this.isOfType("datetime-only")) {
            method.addStatement("$T $L = $L != null ? $T.parse($L, $T.Formatters.DATETIMEONLY.formatter) : null",
                    LocalDateTime.class, to, from, LocalDateTime.class, from, Processor.class
            );
        } else if(this.isOfType("date-only")) {
            method.addStatement("$T $L = $L != null ? $T.parse($L, $T.Formatters.DATEONLY.formatter) : null",
                    LocalDate.class, to, from, LocalDate.class, from, Processor.class
            );
        } else if(this.isOfType("time-only")) {
            method.addStatement("$T $L = $L != null ? $T.parse($L, $T.Formatters.TIMEONLY.formatter) : null",
                    LocalTime.class, to, from, LocalTime.class, from, Processor.class
            );
        } else if(this.isOfType("boolean")) {
            method.addStatement("$T $L = $L != null ? ($S.equals($L) || $S.equals($L) ? $T.TRUE : $T.FALSE) : null",
                    Boolean.class, to, from, "true", from, "1", from, Boolean.class, Boolean.class
            );
        }
    }

}
