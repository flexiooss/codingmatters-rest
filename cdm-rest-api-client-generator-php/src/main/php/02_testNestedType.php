<?php

namespace Test;

use PHPUnit\Framework\TestCase;

class NestedPropertiesTest extends TestCase {

   public function testNestedType(){
        $object = new \org\generated\NestedType();

        $nested = new \org\generated\nestedtype\Nested();
        //$nestedArray = new \org\generated\nestedtype\Nested

        $subNested = new \org\generated\nestedtype\nested\SubNested();

        $nested -> withSubNested( $subNested );

        $object -> withNested( $nested );

        $object -> nested() -> withStringProp( "foo" );
        $object -> nested() -> subNested() -> withStringProp( "bar" );
        $object -> nested() -> subNested() -> withEnumProp( \org\generated\nestedtype\nested\subnested\SubNestedEnumProp::A() );
        $object -> nested() -> subNested() -> withEnumArrayProp( new \org\generated\nestedtype\nested\subnested\SubNestedEnumArrayPropList(
            array( \org\generated\nestedtype\nested\subnested\SubNestedEnumArrayProp::C() )
        ));
        $object -> nested() -> subNested() -> enumArrayProp()[] = \org\generated\nestedtype\nested\subnested\SubNestedEnumArrayProp::B();

        $this -> assertSame( 'foo', $object -> nested() -> stringProp() );
        $this -> assertSame( 'bar',  $object -> nested() -> subNested() -> stringProp() );
        $this -> assertSame( 'A', $object -> nested() -> subNested() -> enumProp() -> value() );
        $this -> assertSame( 'C', $object -> nested() -> subNested() -> enumArrayProp()[0] -> value() );
        $this -> assertSame( 'B', $object -> nested() -> subNested() -> enumArrayProp()[1] -> value() );
    }

}