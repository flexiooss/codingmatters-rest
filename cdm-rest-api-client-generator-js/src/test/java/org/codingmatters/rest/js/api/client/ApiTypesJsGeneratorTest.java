package org.codingmatters.rest.js.api.client;

import org.codingmatters.rest.js.api.client.types.ApiTypesJsGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeList;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumInSpecEnum;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ApiTypesJsGeneratorTest {

    private PackagesConfiguration packageConfig;

    @Before
    public void setUp() throws Exception {
        packageConfig = new PackagesConfiguration( "org.generated.client", "org.generated.api", "org.generated.types" );
    }

    @Test
    public void whenName_then() throws Exception {
        RamlModelResult ramlModel;
        String ramlLocation = Thread.currentThread().getContextClassLoader().getResource( "basicProperties.raml" ).getPath();
        ramlModel = new RamlModelBuilder().buildApi( ramlLocation );

        ApiTypesJsGenerator generator = new ApiTypesJsGenerator( packageConfig );
        List<ParsedValueObject> valueObjects = generator.parseValueObjects( ramlModel );

        assertThat( valueObjects.size(), is( 1 ) );
        ParsedValueObject object = valueObjects.get( 0 );
        assertThat( object.properties().size(), is( 9 ) );

        ValueObjectProperty property = object.properties().get( 0 );
        assertThat( property.name(), is( "simpleString" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = object.properties().get( 1 );
        assertThat( property.name(), is( "stringProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = object.properties().get( 2 );
        assertThat( property.name(), is( "stringArrayProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) (property.type())).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = object.properties().get( 3 );
        assertThat( property.name(), is( "shortStringArray" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.STRING ) );

        property = object.properties().get( 4 );
        assertThat( property.name(), is( "intProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) property.type()).type(), is( YAML_PRIMITIVE_TYPES.INT ) );

        property = object.properties().get( 5 );
        assertThat( property.name(), is( "intArrayProp" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.INT ) );

        property = object.properties().get( 6 );
        assertThat( property.name(), is( "intShortArray" ) );
        assertThat( ((ValueObjectTypePrimitiveType) ((ValueObjectTypeList) property.type()).type()).type(), is( YAML_PRIMITIVE_TYPES.INT ) );

        property = object.properties().get( 7 );
        assertThat( property.name(), is( "enumProp" ) );
        assertThat( ((YamlEnumInSpecEnum) property.type()).values().toArray(), is( new String[]{ "A", "B", "C" } ) );

        property = object.properties().get( 8 );
        assertThat( property.name(), is( "enumArrayProp" ) );
        assertThat( ((YamlEnumInSpecEnum) ((ValueObjectTypeList) property.type()).type()).values().toArray(), is( new String[]{ "D", "E", "F" } ) );
    }

}
