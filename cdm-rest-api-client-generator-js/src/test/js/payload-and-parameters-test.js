import {TestCase} from 'code-altimeter-js'

const assert = require( 'assert' )
import "../org/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";

import {FLEXIO_IMPORT_OBJECT, FlexDate, FlexDateTime, FlexTime} from 'flexio-jshelpers';

class PayloadAndParameterTest extends TestCase {

    testObjectPayload(){
        assert.equal( true, true );
        var requester = new FakeHttpRequester();
        requester._responseHeaders["stringParam"] = "resp-stringParam";
        requester._responseHeaders["intParam"] = "7";
        requester._responseHeaders["floatParam"] = "5.4";
        requester._responseHeaders["dateParam"] = "1993-10-17";
        requester._responseHeaders["datetimeParam"] = "1992-10-17T18:42:07";
        requester._responseHeaders["timeParam"] = "17:14:12";
        requester._responseHeaders["boolParam"] = "true";

        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.SimpleResourcesAPIClient( requester, "http://gateway" );
        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.HeaderParamsGetRequestBuilder();
        request.uriParams( "myUriParam" );
        request.stringParam( "myStringParam" );
        // request.stringArrayParam
        request.intParam( 7 );
        // request.intArrayParam
        request.floatParam( 7.1 );
        // request.floatArrayParam
        request.dateParam( new FlexDate( "1992-10-17" ) );
        // request.dateArrayParam
        request.datetimeParam( new FlexDateTime( "1992-10-17T14:12:07" ) );
        // request.datetimeArrayParam
        request.timeParam( new FlexTime( "14:12:07" ) );
        // request.timeArrayParam
        request.boolParam( true );
        // request.boolArrayParam

        var response = client.headerParams().headerParamsGet( request.build() );

        assert.deepStrictEqual( response.status200().stringParam(), "resp-stringParam" );
        assert.deepStrictEqual( response.status200().intParam(), 7 );
        assert.deepStrictEqual( response.status200().floatParam(), 5.4 );
        assert.deepStrictEqual( response.status200().dateParam().toJSON(), "1993-10-17" );
        assert.deepStrictEqual( response.status200().datetimeParam().toJSON(), "1992-10-17T18:42:07" );
        assert.deepStrictEqual( response.status200().timeParam().toJSON(), "17:14:12" );
        assert.deepStrictEqual( response.status200().boolParam(), true );
    }
}

runTest( PayloadAndParameterTest );