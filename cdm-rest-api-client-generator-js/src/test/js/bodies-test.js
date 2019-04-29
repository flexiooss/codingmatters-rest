import {TestCase} from 'code-altimeter-js'

const assert = require( 'assert' )
import "../org/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";

import {globalScope, FLEXIO_IMPORT_OBJECT, FlexDate, FlexDateTime, FlexTime} from 'flexio-jshelpers';

class BodiesTest extends TestCase {

    testTypePayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '{"name":"Morillo"}' );

        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.TypePostRequestBuilder();
        var littleObj = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj.name( "Jungle Patrol" );
        request.payload( littleObj.build() );

        var response = client.type().typePost( request.build() );
        assert.equal( response.status200().payload().name(), "Morillo" );
        assert.equal( requester.lastBody(), '{"name":"Jungle Patrol"}' );
    }

    testTypeArrayShortPayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '[{"name":"Morillo"}]' );
        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.TypeArrayShortPostRequestBuilder();
        var littleObj1 = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj1.name( "Morillo" );
        var littleObj2 = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj2.name( "Jungle Patrol" );

        var list = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.typearrayshortpostrequest.TypeArrayShortPostRequestPayloadList( littleObj1.build(), littleObj2.build() );
        request.payload( list );

        var response = client.typeArrayShort().typeArrayShortPost( request.build() );
        console.log( response.status200().payload() );
        assert.equal( response.status200().payload()[0].name(), "Morillo" );
        assert.equal( requester.lastBody(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]' );

        var response = globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.typearrayshortpostresponse.Status200Builder.fromJson( '{"payload":[{"name":"Morillo"},{"name":"Jungle Patrol"}]}' );
        console.log( "HELLLL" );
        console.log( JSON.stringify( response ));
    }

    testTypeArrayPayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '[{"name":"Morillo"}]' );
        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.TypeArrayPostRequestBuilder();
        var littleObj1 = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj1.name( "Morillo" );
        var littleObj2 = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj2.name( "Jungle Patrol" );

        var list = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.typearraypostrequest.TypeArrayPostRequestPayloadList( littleObj1.build(), littleObj2.build() );
        request.payload( list );

        var response = client.typeArray().typeArrayPost( request.build() );
        assert.equal( response.status200().payload()[0].name(), "Morillo" );
        assert.equal( requester.lastBody(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]' );
    }

    testObjectPayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '{"Romare":"The Blues"}' );
        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.ObjectPostRequestBuilder();
        var obj1 = {};
        obj1['High'] = 'Klassified'
        obj1['1250'] = 1919
        request.payload( obj1 );

        var response = client.object().objectPost( request.build() );
        assert.equal( requester.lastBody(), '{"1250":1919,"High":"Klassified"}' );
        assert.equal( response.status200().payload()["Romare"], "The Blues" );
    }

    testObjectArrayShortPayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '[{"Romare":"The Blues"},{"Eprom":"9 To Ya Dome"}]' );
        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.ObjectArrayShortPostRequestBuilder();
        var obj1 = {};
        obj1['High'] = 'Klassified';
        var obj2 = {};
        obj2['1250'] = 1919;

        request.payload( new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.objectarrayshortpostrequest.ObjectArrayShortPostRequestPayloadList( obj1, obj2 ) );

        var response = client.objectArrayShort().objectArrayShortPost( request.build() );
        assert.equal( requester.lastBody(), '[{"High":"Klassified"},{"1250":1919}]' );
        assert.equal( response.status200().payload()[0]["Romare"], "The Blues" );
        assert.equal( response.status200().payload()[1]["Eprom"], "9 To Ya Dome" );
    }

    testObjectArrayPayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '[{"Romare":"The Blues"},{"Eprom":"9 To Ya Dome"}]' );
        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.ObjectArrayPostRequestBuilder();
        var obj1 = {};
        obj1['High'] = 'Klassified';
        var obj2 = {};
        obj2['1250'] = 1919;

        request.payload( new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.objectarraypostrequest.ObjectArrayPostRequestPayloadList( obj1, obj2 ) );

        var response = client.objectArray().objectArrayPost( request.build() );
        assert.equal( requester.lastBody(), '[{"High":"Klassified"},{"1250":1919}]' );
        assert.equal( response.status200().payload()[0]["Romare"], "The Blues" );
        assert.equal( response.status200().payload()[1]["Eprom"], "9 To Ya Dome" );
    }

    testFilePayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( 'hello' );
        var client = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new globalScope[FLEXIO_IMPORT_OBJECT].org.generated.api.FilePostRequestBuilder();

        request.payload( "this is binary data" );
        request.contentType( "Toto" );

        var response = client.file().filePost( request.build() );
        assert.equal( response.status200().payload(), "hello" );
        assert.equal( requester._lastContentType, "Toto" );
        assert.equal( response.status200().contentType(), "Shit" );
        assert.equal( requester.lastBody(), "this is binary data" );
    }

}
runTest( BodiesTest );