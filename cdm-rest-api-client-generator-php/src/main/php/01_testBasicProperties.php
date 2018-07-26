<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\generated\SimplePropertyType;
use org\generated\simplepropertytype\SimplePropertyTypeStringArrayPropList;
use org\generated\simplepropertytype\SimplePropertyTypeShortStringArrayList;
use org\generated\simplepropertytype\SimplePropertyTypeIntArrayPropList;
use org\generated\simplepropertytype\SimplePropertyTypeIntShortArrayList;
use org\generated\simplepropertytype\SimplePropertyTypeEnumProp;
use org\generated\simplepropertytype\SimplePropertyTypeEnumArrayPropList;
use org\generated\simplepropertytype\SimplePropertyTypeEnumArrayProp;

class BasicPropertiesTest extends TestCase {

    public function testBasicPropertiesTypes(){
        $type = new SimplePropertyType();
        $type -> withSimpleString( "toto" );
        $type -> withStringProp( "toto" );
        $type -> withStringArrayProp( new SimplePropertyTypeStringArrayPropList( array("foo", "bar" )));
        $type -> withShortStringArray( new SimplePropertyTypeShortStringArrayList( array("foo", "bar" )));
        $type -> withIntProp( 7 );
        $type -> withIntArrayProp( new SimplePropertyTypeIntArrayPropList( array ( 7, 9 )));
        $type -> withIntShortArray( new SimplePropertyTypeIntShortArrayList( array( 9, 5 )));
        $type -> withEnumProp( SimplePropertyTypeEnumProp::B() );
        $type -> withEnumArrayProp( new SimplePropertyTypeEnumArrayPropList( array( SimplePropertyTypeEnumArrayProp::E() )));

        $type -> enumArrayProp()[] = SimplePropertyTypeEnumArrayProp::D();

        $this -> assertSame( "toto", $type -> simpleString() );
        $this -> assertSame( "toto", $type -> stringProp() );
        $this -> assertSame( "foo", $type -> stringArrayProp()[0] );
        $this -> assertSame( "bar", $type -> stringArrayProp()[1] );
        $this -> assertSame( "foo", $type -> shortStringArray()[0] );
        $this -> assertSame( "bar", $type -> shortStringArray()[1] );
        $this -> assertSame( 7, $type -> intProp() );
        $this -> assertSame( 7, $type -> intArrayProp()[0] );
        $this -> assertSame( 9, $type -> intArrayProp()[1] );
        $this -> assertSame( 9, $type -> intShortArray()[0] );
        $this -> assertSame( 5, $type -> intShortArray()[1] );

        $this -> assertSame( 'E', $type -> enumArrayProp()[0]->value() );
        $this -> assertSame( 'D', $type -> enumArrayProp()[1]->value() );
    }

}