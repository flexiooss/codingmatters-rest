package org.codingmatters.rest.api.generator.utils;

import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Parameter {
    static private final Logger log = LoggerFactory.getLogger(Parameter.class);

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

    public enum ParameterSource {
        HEADERS("headers", "header"), QUERY("queryParameters", "parameter"), URI("uriParameters", null);

        public final String delegateMethod;
        public final String requesterMethod;


        ParameterSource(String delegateMethod, String requesterMethod) {
            this.delegateMethod = delegateMethod;
            this.requesterMethod = requesterMethod;
        }
    }

    private final Naming naming;
    private final TypeDeclaration typeDeclaration;

    public Parameter(Naming naming, TypeDeclaration typeDeclaration) {
        this.naming = naming;
        this.typeDeclaration = typeDeclaration;
    }

    protected String name() {
        return this.typeDeclaration.name();
    }

    protected String type() {
        return this.typeDeclaration.type();
    }



    protected boolean isSupportedType() {
        return SUPPPORTED_RAML_TYPES.contains(this.ramlType().toLowerCase());
    }

    protected String ramlType() {
        String t;
        if(this.isArray()) {
            t = ((ArrayTypeDeclaration)typeDeclaration).items().type().toLowerCase();
        } else {
            t = this.typeDeclaration.type();
        }
        return t;
    }

    protected String property() {
        return this.naming.property(typeDeclaration.name());
    }

    protected boolean isOfType(String ramlTypeName) {
        return this.ramlType().equalsIgnoreCase(ramlTypeName);
    }

    protected boolean isArray() {
        return this.typeDeclaration.type().equalsIgnoreCase("array");
    }
}
