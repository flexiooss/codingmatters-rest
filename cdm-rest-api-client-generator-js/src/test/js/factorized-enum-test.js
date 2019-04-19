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
//
//    testEnumObjectBody(){
//        var requester = new FakeHttpRequester();
//        requester.nextBody( '{"toto":"DC","totoList":["DC","DC"],"totoListShort":["AC","DC"]}' );
//        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.FactorizedEnumsAPIClient( requester, "http://gateway" );
//
//        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.TotoPostRequestBuilder();
//
//        var myEnum1 =  window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyEnum.DC;
//        var myEnum2 =  window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyEnum.DC;
//        var myClass =  new window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyClassBuilder();
//        myClass.toto( myEnum1 );
//        myClass.totoList( new window[FLEXIO_IMPORT_OBJECT].org.generated.types.myclass.MyClassTotoListList( myEnum1, myEnum2 )  );
//        myClass.totoListShort( new window[FLEXIO_IMPORT_OBJECT].org.generated.types.myclass.MyClassTotoListShortList( myEnum1, myEnum2 ) );
//
//        request.payload( myClass.build() );
//        var response = client.toto().totoPost( request.build() );
//
//        assert.equal( response.status200().payload().toto().name, "DC" );
//        assert.equal( response.status200().payload().totoList()[0].name, "DC" );
//        assert.equal( response.status200().payload().totoList()[1].name, "DC" );
//        assert.equal( response.status200().payload().totoListShort()[0].name, "AC" );
//        assert.equal( response.status200().payload().totoListShort()[1].name, "DC" );
//
//        assert.equal( requester.lastBody(), '{"toto":"DC","totoList":["DC","DC"],"totoListShort":["DC","DC"]}' );
//
//    }
//
//    testEnumBody(){
//        var myEnum1 =  window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyEnum.DC;
//        var myEnum2 =  window[FLEXIO_IMPORT_OBJECT].org.generated.types.MyEnum.DC;
//
//        var requester = new FakeHttpRequester();
//        requester.nextBody( 'AC' );
//        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.FactorizedEnumsAPIClient( requester, "http://gateway" );
//
//        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.YoyoPostRequestBuilder();
//        request.payload( myEnum1 );
//
//        var response = client.toto().totoPost( request.build() );
//
//        assert.equal( response.status200().payload().name, "AC" );
//
//    }

}
runTest( FactorizedEnumTest );