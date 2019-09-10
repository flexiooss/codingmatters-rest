import {TestCase} from 'code-altimeter-js'
import "../org/generated/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";
import { globalFlexioImport } from '@flexio-oss/global-import-registry'
import { FlexDate, FlexDateTime, FlexTime } from '@flexio-oss/flex-types';

const assert = require('assert')


class FactorizedEnumTest extends TestCase {

    testTypeConstruction(){
        let myEnum1 =  globalFlexioImport.org.generated.api.types.MyEnum.AC;
        let myEnum2 =  globalFlexioImport.org.generated.api.types.MyEnum.DC;
        let myClass =  new globalFlexioImport.org.generated.api.types.MyClassBuilder();
        myClass.toto( myEnum1 );
        myClass.totoList( new globalFlexioImport.org.generated.api.types.myclass.MyClassTotoListList( myEnum1, myEnum2 )  );
        myClass.totoListShort( new globalFlexioImport.org.generated.api.types.myclass.MyClassTotoListShortList( myEnum1, myEnum2 ) );

        let json = '{"toto":"AC","totoList":["AC","DC"],"totoListShort":["AC","DC"]}';
        assert.equal( JSON.stringify( myClass.build() ), json );
    }

    testDeserialization(){
        let json = '{"toto":"AC","totoList":["AC","DC"],"totoListShort":["AC","DC"]}';
        let myClass = globalFlexioImport.org.generated.api.types.MyClassBuilder.fromJson( json ).build();

        assert.equal( myClass.toto().name, "AC" );
        assert.equal( myClass.totoList()[0].name, "AC" )
        assert.equal( myClass.totoList()[1].name, "DC" )
    }
//
//    testEnumObjectBody(){
//        let requester = new FakeHttpRequester();
//        requester.nextBody( '{"toto":"DC","totoList":["DC","DC"],"totoListShort":["AC","DC"]}' );
//        let client = new globalFlexioImport.org.generated.client.FactorizedEnumsAPIClient( requester, "http://gateway" );
//
//        let request = new globalFlexioImport.org.generated.api.TotoPostRequestBuilder();
//
//        let myEnum1 =  globalFlexioImport.org.generated.api.types.MyEnum.DC;
//        let myEnum2 =  globalFlexioImport.org.generated.api.types.MyEnum.DC;
//        let myClass =  new globalFlexioImport.org.generated.api.types.MyClassBuilder();
//        myClass.toto( myEnum1 );
//        myClass.totoList( new globalFlexioImport.org.generated.api.types.myclass.MyClassTotoListList( myEnum1, myEnum2 )  );
//        myClass.totoListShort( new globalFlexioImport.org.generated.api.types.myclass.MyClassTotoListShortList( myEnum1, myEnum2 ) );
//
//        request.payload( myClass.build() );
//        let response = client.toto().totoPost( request.build() );
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
//        let myEnum1 =  globalFlexioImport.org.generated.api.types.MyEnum.DC;
//        let myEnum2 =  globalFlexioImport.org.generated.api.types.MyEnum.DC;
//
//        let requester = new FakeHttpRequester();
//        requester.nextBody( 'AC' );
//        let client = new globalFlexioImport.org.generated.client.FactorizedEnumsAPIClient( requester, "http://gateway" );
//
//        let request = new globalFlexioImport.org.generated.api.YoyoPostRequestBuilder();
//        request.payload( myEnum1 );
//
//        let response = client.toto().totoPost( request.build() );
//
//        assert.equal( response.status200().payload().name, "AC" );
//
//    }

}
runTest( FactorizedEnumTest );
