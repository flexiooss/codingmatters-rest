package org.codingmatters.rest.parser;

import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;

import java.util.Arrays;
import java.util.Optional;

public enum RAML_PRIMITIVE_TYPES {
    STRING( "string", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.STRING ),
    INTEGER( "integer", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.LONG ),
    NUMBER( "number", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.FLOAT ),
    DATE( "date-only", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE ),
    TIME( "time-only", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TIME ),
    DATE_TIME( "datetime-only", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.DATE_TIME ),
    TZ_DATE_TIME( "datetime", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.TZ_DATE_TIME ),
    FILE( "file", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BYTES ),
    BOOLEAN( "boolean", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.BOOL ),
    OBJECT( "object", ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.OBJECT );


    RAML_PRIMITIVE_TYPES( String ramlName, ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES yamlValue ) {
        this.ramlName = ramlName;
        this.yamlValue = yamlValue;
    }

    private final String ramlName;
    private final ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES yamlValue;

    public ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES toYaml() {
        return this.yamlValue;
    }

    public static Optional<RAML_PRIMITIVE_TYPES> fromRaml( String ramlName ) {
        return Arrays.stream( RAML_PRIMITIVE_TYPES.values() ).filter( type->type.ramlName.equals( ramlName ) ).findFirst();
    }

}
