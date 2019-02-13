import {TestCase} from 'code-altimeter-js'
const assert = require('assert')
import "../org/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";

class PayloadAndParameterTest extends TestCase {

    testObjectPayload(){
        var requester = new FakeHttpRequester();
        var client = new window.FLEXIO_IMPORT_OBJECT.org.generated.client.SimpleResourcesAPIClient( requester, "http://gateway" );
        var request = new window.FLEXIO_IMPORT_OBJECT.org.generated.api.SimpleObjectPostRequestBuilder();
        var object = {};
        request.payload( object );
        var response = client.simpleObject().simpleObjectPost( request.build() );
        assert.notStrictEqual( response.status200(), null );
    }

}
runTest( PayloadAndParameterTest );