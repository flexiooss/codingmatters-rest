package org.codingmatters.rest.api.generator.client.caller;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class CallerParam extends Parameter {

    static private final Logger log = LoggerFactory.getLogger(CallerParam.class);

    public CallerParam(ResourceNaming naming, TypeDeclaration param) {
        super(naming, param);
    }

    public void addStatement(MethodSpec.Builder method, ParameterSource source) {
        if (this.isSupportedType()) {
            method.beginControlFlow("if(request.$L() != null)", this.property());

            if(source != ParameterSource.URI) {
                if (this.isArray()) {
                    method.addStatement("$T<$T> $L = new $T<>()", List.class, String.class, this.property(), LinkedList.class);

                    method.beginControlFlow("for($T $L : request.$L())", this.javaType(), this.property() + "RawElement", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "RawElement", this.property() + "Element");
                    method.addStatement("$L.add($L)", this.property(), this.property() + "Element");
                    method.endControlFlow();
                } else {
                    method.addStatement("$T $L = request.$L()", this.javaType(), this.property() + "Raw", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "Raw", this.property());
                }
                method.addStatement("requester.$L($S, $L)", source.requesterMethod, this.name(), this.property());
            } else {
                if(this.isArray()) {
                    method.beginControlFlow("for($T $L : request.$L())", this.javaType(), this.property() + "RawElement", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "RawElement", this.property() + "Element");
                    method.addStatement("path = path.replaceFirst($S, $L)", "\\{" + this.name() + "\\}", this.property() + "Element");
                    method.endControlFlow();
                } else {
                    method.addStatement("$T $L = request.$L()", this.javaType(), this.property() + "Raw", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "Raw", this.property());
                    method.addStatement("path = path.replaceFirst($S, $L)", "\\{" + this.name() + "\\}", this.property());
                }
            }

            method.endControlFlow();
        } else {
            log.error("not yet implemented parameter : name={} type={}", this.name(), this.type());
        }
    }

    private Class javaType() {
        if(this.isOfType("string")) {
            return String.class;
        } else if(this.isOfType("integer")) {
            return Long.class;
        } else if(this.isOfType("number")) {
            return Double.class;
        } else if(this.isOfType("datetime-only")) {
            return LocalDateTime.class;
        } else if(this.isOfType("date-only")) {
            return LocalDate.class;
        } else if(this.isOfType("time-only")) {
            return LocalTime.class;
        } else if(this.isOfType("boolean")) {
            return Boolean.class;
        } else {
            return Object.class;
        }
    }


    protected void addTranstypeToStringStatement(MethodSpec.Builder method, String from, String to) {
        if(this.isOfType("string")) {
            method.addStatement("$T $L = $L", String.class, to, from);
        } else if(this.isOfType("integer") ||
                this.isOfType("number") ||
                this.isOfType("boolean")
                ) {
            method.addStatement("$T $L = $L != null ? $L.toString() : null", String.class, to, from, from);
        } else if(this.isOfType("datetime-only")) {
            method.addStatement("$T $L = $L != null ? $L.format($T.Formatters.DATETIMEONLY.formatter) : null",
                    String.class, to, from, from, Requester.class
            );
        } else if(this.isOfType("date-only")) {
            method.addStatement("$T $L = $L != null ? $L.format($T.Formatters.DATEONLY.formatter) : null",
                    String.class, to, from, from, Requester.class
            );
        } else if(this.isOfType("time-only")) {
            method.addStatement("$T $L = $L != null ? $L.format($T.Formatters.TIMEONLY.formatter) : null",
                    String.class, to, from, from, Requester.class
            );
        }
    }
}
