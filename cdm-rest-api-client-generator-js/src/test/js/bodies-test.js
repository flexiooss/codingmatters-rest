import {TestCase} from 'code-altimeter-js'

const assert = require( 'assert' )
import "../org/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";

import {FLEXIO_IMPORT_OBJECT, FlexDate, FlexDateTime, FlexTime} from 'flexio-jshelpers';

class BodiesTest extends TestCase {

    testTypePayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '{"name":"Morillo"}' );

        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.TypePostRequestBuilder();
        var littleObj = new window[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj.name( "Jungle Patrol" );
        request.payload( littleObj.build() );

        var response = client.type().typePost( request.build() );
        assert.equal( response.status200().payload().name(), "Morillo" );
        assert.equal( requester.lastBody(), '{"name":"Jungle Patrol"}' );
    }

    testTypeArrayShortPayload(){
        var requester = new FakeHttpRequester();
        requester.nextBody( '[{"name":"Morillo"}]' );
        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.RequestBodiesAPIClient( requester, "http://gateway" );

        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.TypeArrayShortPostRequestBuilder();
        var littleObj1 = new window[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj1.name( "Morillo" );
        var littleObj2 = new window[FLEXIO_IMPORT_OBJECT].org.generated.types.LittleObjectBuilder();
        littleObj2.name( "Jungle Patrol" );

        var list = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.typearrayshortpostrequest.TypeArrayShortPostRequestPayloadList( littleObj1.build(), littleObj2.build() );
        request.payload( list );

        var response = client.typeArrayShort().typeArrayShortPost( request.build() );
        console.log( response.status200().payload() );
        assert.equal( response.status200().payload()[0].name(), "Morillo" );
        assert.equal( requester.lastBody(), '[{"name":"Morillo"},{"name":"Jungle Patrol"}]' );
    }

}
runTest( BodiesTest );