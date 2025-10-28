package org.codingmatters.rest.api.generator.client.caller;

import com.squareup.javapoet.MethodSpec;
import org.codingmatters.rest.api.client.Requester;
import org.codingmatters.rest.api.generator.client.ResourceNaming;
import org.codingmatters.rest.api.generator.utils.Parameter;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class CallerParam extends Parameter {

    static private final Logger log = LoggerFactory.getLogger(CallerParam.class);

    public enum Encoding {
        NONE,
        URL
    }

    private final Encoding encoding;

    public CallerParam(ResourceNaming naming, TypeDeclaration param, Encoding encoding) {
        super(naming, param);
        this.encoding = encoding;
    }

    public void addStatement(MethodSpec.Builder method, ParameterSource source) {
        if (this.isSupportedType()) {
            if (source != ParameterSource.URI) {
                method.beginControlFlow("if (request.$L() != null)", this.property());
                if (this.isArray()) {
                    method.addStatement("$T<$T> $L = new $T<>()", List.class, String.class, this.property(), LinkedList.class);

                    method.beginControlFlow("for ($T $L : request.$L())", this.javaType(), this.property() + "RawElement", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "RawElement", this.property() + "Element");
                    method.addStatement("$L.add($L)", this.property(), this.property() + "Element");
                    method.endControlFlow();
                } else {
                    method.addStatement("$T $L = request.$L()", this.javaType(), this.property() + "Raw", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "Raw", this.property());
                }
                method.addStatement("requester.$L($S, $L)", source.requesterMethod, this.name(), this.property());
                method.endControlFlow();
            } else {
                method.beginControlFlow("if (request.$L() != null)", this.property());
                if (this.isArray()) {
                    method.beginControlFlow("for ($T $L : request.$L())", this.javaType(), this.property() + "RawElement", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "RawElement", this.property() + "Element");
                    method.addStatement("path = path.replaceFirst($S, $L)", "\\{" + this.name() + "\\}", this.property() + "Element");
                    method.endControlFlow();
                } else {
                    method.addStatement("$T $L = request.$L()", this.javaType(), this.property() + "Raw", this.property());
                    this.addTranstypeToStringStatement(method, this.property() + "Raw", this.property());
                    method.addStatement("path = path.replaceFirst($S, $L)", "\\{" + this.name() + "\\}", this.property());
                }
                method.nextControlFlow("else")
                        .addStatement("throw new $T($S)", Requester.MissingUriParameterException.class, "missing mandatory uri parameter : " + this.property())
                ;
                method.endControlFlow();
            }

        } else {
            log.error("not yet implemented parameter : name={} type={}", this.name(), this.type());
        }
    }


    protected void addTranstypeToStringStatement(MethodSpec.Builder method, String from, String to) {
        if (this.isOfType("string")) {
            method.addStatement("$T $L = $L", String.class, to, from);
        } else {
            method.addStatement("$T $L = $L != null ? $L.toString() : null", String.class, to, from, from);
        }
        if (this.encoding.equals(Encoding.URL)) {
            method
                    .beginControlFlow("try")
                    .addStatement("$L = $L != null ? $T.encode($L, $S) : null", to, to, URLEncoder.class, to, "utf-8")
                    .nextControlFlow("catch ($T e)", UnsupportedEncodingException.class)
                    .addStatement("throw new $T($S + $L, e)", IOException.class, "failed encoding uri parameter : ", to)
                    .endControlFlow();
        }
    }
}
