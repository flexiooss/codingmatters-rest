import {TestCase} from 'code-altimeter-js'

const assert = require( 'assert' )
import "../org/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";

import {FLEXIO_IMPORT_OBJECT, FlexDate, FlexDateTime, FlexTime} from 'flexio-jshelpers';

class FactorizedEnumTest extends TestCase {

    testTypeConstruction(){
        var myEnum1 =  window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyEnum.AC;
        var myEnum2 =  window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyEnum.DC;
        var myClass =  new window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyClassBuilder();
        myClass.toto( myEnum1 );
        myClass.totoList( new window[FLEXIO_IMPORT_OBJECT].org.generated.types.myclass.MyClassTotoListList( myEnum1, myEnum2 )  );
        myClass.totoListShort( new window[FLEXIO_IMPORT_OBJECT].org.generated.types.myclass.MyClassTotoListShortList( myEnum1, myEnum2 ) );

        var json = '{"toto":"AC","totoList":["AC","DC"],"totoListShort":["AC","DC"]}';
        assert.equal( JSON.stringify( myClass.build() ), json );
    }

    testDeserialization(){
        var json = '{"toto":"AC","totoList":["AC","DC"],"totoListShort":["AC","DC"]}';
        var myClass = window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyClassBuilder.fromJson( json ).build();

        assert.equal( myClass.toto().name, "AC" );
        assert.equal( myClass.totoList()[0].name, "AC" )
        assert.equal( myClass.totoList()[1].name, "DC" )
    }

}
runTest( FactorizedEnumTest );