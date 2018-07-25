package org.codingmatters.rest.api.generator.type;

import org.codingmatters.rest.api.generator.exception.RamlSpecException;
import org.codingmatters.value.objects.spec.TypeToken;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by nelt on 5/3/17.
 */
public enum RamlType {
    STRING(TypeToken.STRING.getImplementationType()),
    INTEGER(TypeToken.LONG.getImplementationType()),
    NUMBER(TypeToken.DOUBLE.getImplementationType()),
    BOOLEAN(TypeToken.BOOL.getImplementationType()),
    DATE_ONLY(TypeToken.DATE.getImplementationType()),
    TIME_ONLY(TypeToken.TIME.getImplementationType()),
    DATETIME_ONLY(TypeToken.DATE_TIME.getImplementationType()),
    DATETIME(TypeToken.TZ_DATE_TIME.getImplementationType())
    ;

    private final String javaType;

    RamlType(String javaType) {
        this.javaType = javaType;
    }

    public String javaType() {
        return this.javaType;
    }

    static public boolean isRamlType(TypeDeclaration declaration) {
        for (RamlType ramlType : RamlType.values()) {
            if(ramlType.name().equals(ramlTypeName(declaration))) {
                return true;
            }
        }
        return false;
    }

    static public RamlType from(TypeDeclaration declaration) throws RamlSpecException {
        try {
            return RamlType.valueOf(ramlTypeName(declaration));
        } catch(IllegalArgumentException e) {
            throw new RamlSpecException("not implemented type : " + declaration.type(), e);
        }
    }

    static private String ramlTypeName(TypeDeclaration declaration) {
        String name = declaration.type();
        if( name == null ){
            name = declaration.name();
        }
        while(name.endsWith("[]")) {
            name = name.substring(0, name.length() - 2);
        }
        return name.replaceAll("-", "_").toUpperCase();
    }

    static public boolean isArrayType(TypeDeclaration declaration) {
        return declaration.type().endsWith("[]");
    }
}
