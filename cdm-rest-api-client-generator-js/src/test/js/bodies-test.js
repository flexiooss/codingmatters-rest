import {TestCase} from 'code-altimeter-js'
import "../org/generated/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";
import { globalFlexioImport } from '@flexio-oss/global-import-registry'
import { FlexDate, FlexDateTime, FlexTime} from '@flexio-oss/flex-types';
import "@flexio-oss/flex-types"

const assert = require('assert')

class BodiesTest extends TestCase {

    testTypePayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( '{"name":"Morillo"}' );

        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.TypePostRequestBuilder();
        let littleObj = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder();
        littleObj.name( "Jungle Patrol" );
        request.payload( littleObj.build() );

        client.type().typePost( request.build(), (response) => {
            assert.equal( response.status200().payload().name(), "Morillo" );
            assert.equal( requester.lastBody(), '{"name":"Jungle Patrol"}' );
        });
    }

    testTypeArrayShortPayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( '[{"name":"Morillo"}]' );
        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.TypeArrayShortPostRequestBuilder();
        let littleObj1 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder();
        littleObj1.name( "Morillo" );
        let littleObj2 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder();
        littleObj2.name( "Jungle Patrol" );

        let list = new globalFlexioImport.org.generated.api.types.LittleObjectList( littleObj1.build(), littleObj2.build() );
        request.payload( list );

        client.typeArrayShort().typeArrayShortPost( request.build(),(response)=>{
            assert.equal( response.status200().payload()[0].name(), "Morillo" );
            assert.equal( requester.lastBody(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]' );
        } );
    }

    testTypeArrayPayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( '[{"name":"Morillo"}]' );
        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.TypeArrayPostRequestBuilder();
        let littleObj1 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder();
        littleObj1.name( "Morillo" );
        let littleObj2 = new globalFlexioImport.org.generated.api.types.LittleObjectBuilder();
        littleObj2.name( "Jungle Patrol" );

        let list = new globalFlexioImport.org.generated.api.types.LittleObjectList( littleObj1.build(), littleObj2.build() );
        request.payload( list );

        client.typeArray().typeArrayPost( request.build(),(response)=>{
            assert.equal( response.status200().payload()[0].name(), "Morillo" );
            assert.equal( requester.lastBody(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]' );
        } );
    }

    testObjectPayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( '{"Romare":"The Blues"}' );
        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.ObjectPostRequestBuilder();
        let obj1 = {};
        obj1['High'] = 'Klassified'
        obj1['1250'] = 1919
        request.payload( obj1 );

        client.object().objectPost( request.build(),(response)=>{
            assert.equal( requester.lastBody(), '{"1250":1919,"High":"Klassified"}' );
            assert.equal( response.status200().payload()["Romare"], "The Blues" );
        } );
    }

    testObjectArrayShortPayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( '[{"Romare":"The Blues"},{"Eprom":"9 To Ya Dome"}]' );
        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.ObjectArrayShortPostRequestBuilder();
        let obj1 = {};
        obj1['High'] = 'Klassified';
        let obj2 = {};
        obj2['1250'] = 1919;

        request.payload( new globalFlexioImport.io.flexio.flex_types.arrays.ObjectArray( obj1, obj2 ) );

        client.objectArrayShort().objectArrayShortPost( request.build(),(response)=>{
            assert.equal( requester.lastBody(), '[{"High":"Klassified"},{"1250":1919}]' );
            assert.equal( response.status200().payload()[0]["Romare"], "The Blues" );
            assert.equal( response.status200().payload()[1]["Eprom"], "9 To Ya Dome" );
        });
    }

    testObjectArrayPayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( '[{"Romare":"The Blues"},{"Eprom":"9 To Ya Dome"}]' );
        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.ObjectArrayPostRequestBuilder();
        let obj1 = {};
        obj1['High'] = 'Klassified';
        let obj2 = {};
        obj2['1250'] = 1919;

        request.payload( new globalFlexioImport.io.flexio.flex_types.arrays.ObjectArray( obj1, obj2 ) );

        client.objectArray().objectArrayPost( request.build(),(response)=>{
            assert.equal( requester.lastBody(), '[{"High":"Klassified"},{"1250":1919}]' );
            assert.equal( response.status200().payload()[0]["Romare"], "The Blues" );
            assert.equal( response.status200().payload()[1]["Eprom"], "9 To Ya Dome" );
        } );
    }


    testFilePayload(){
        let requester = new FakeHttpRequester();
        requester.nextBody( 'hello' );
        let client = new globalFlexioImport.org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        let request = new globalFlexioImport.org.generated.api.FilePostRequestBuilder();

        request.payload( "this is binary data" );
        request.contentType( "Toto" );

        client.file().filePost( request.build(),(response)=>{
            assert.equal( response.status200().payload(), "hello" );
            assert.equal( requester._lastContentType, "Toto" );
            assert.equal( response.status200().contentType(), "Shit" );
            assert.equal( requester.lastBody(), "this is binary data" );
        } );
    }

}
runTest( BodiesTest );
