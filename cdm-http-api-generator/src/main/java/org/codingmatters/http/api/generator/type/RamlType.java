package org.codingmatters.http.api.generator.type;

import org.codingmatters.http.api.generator.exception.RamlSpecException;
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

    static public RamlType from(TypeDeclaration declaration) throws RamlSpecException {
        try {
            return RamlType.valueOf(declaration.type().replaceAll("-", "_").toUpperCase());
        } catch(IllegalArgumentException e) {
            throw new RamlSpecException("not implemented type : " + declaration.type(), e);
        }
    }
}
