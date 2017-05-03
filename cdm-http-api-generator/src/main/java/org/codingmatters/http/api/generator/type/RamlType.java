package org.codingmatters.http.api.generator.type;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by nelt on 5/3/17.
 */
public enum RamlType {

    STRING(String.class.getName()), INTEGER(Long.class.getName());

    private final String javaType;

    RamlType(String javaType) {
        this.javaType = javaType;
    }

    public String javaType() {
        return this.javaType;
    }

    static public RamlType from(TypeDeclaration declaration) throws RamlSpecException {
        try {
            return RamlType.valueOf(declaration.type().toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new RamlSpecException("not implemented type : " + declaration.type(), e);
        }
    }
}
