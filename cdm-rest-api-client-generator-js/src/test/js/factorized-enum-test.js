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
        myClass.totoList( new globalFlexioImport.org.generated.api.types.MyEnumList( myEnum1, myEnum2 )  );
        myClass.totoListShort( new globalFlexioImport.org.generated.api.types.MyEnumList( myEnum1, myEnum2 ) );

        let json = '{"toto":"AC","totoList":["AC","DC"],"totoListShort":["AC","DC"]}';
        assert.equal( JSON.stringify( myClass.build() ), json );
    }

    testDeserialization(){
        let json = '{"toto":"AC","totoList":["AC","DC"],"totoListShort":["AC","DC"]}';
        let myClass = globalFlexioImport.org.generated.api.types.MyClassBuilder.fromJson( json ).build();

        assert.equal( myClass.toto().name(), "AC" );
        assert.equal( myClass.totoList()[0].name(), "AC" )
        assert.equal( myClass.totoList()[1].name(), "DC" )
    }
}
runTest( FactorizedEnumTest );
