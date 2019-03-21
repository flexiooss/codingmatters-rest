import {TestCase} from 'code-altimeter-js'

const assert = require( 'assert' )
import "../org/package"
import {FakeHttpRequester} from "./utils/FakeHttpRequester";

import {FLEXIO_IMPORT_OBJECT, FlexDate, FlexDateTime, FlexTime} from 'flexio-jshelpers';

class PayloadAndParameterTest extends TestCase {

    testObjectPayload(){
        var requester = new FakeHttpRequester();
        requester._responseHeaders["stringParam"] = "resp-stringParam";
        requester._responseHeaders["stringArrayParam"] = [ "v1", "v2" ];
        requester._responseHeaders["intParam"] = "7";
        requester._responseHeaders["intArrayParam"] = [ "7", "8" ];
        requester._responseHeaders["floatParam"] = "5.4";
        requester._responseHeaders["floatArrayParam"] = [ "5.4", "5.6" ];
        requester._responseHeaders["dateParam"] = "1993-10-17";
        requester._responseHeaders["dateArrayParam"] = [ "1993-10-17", "1992-10-17" ];
        requester._responseHeaders["datetimeParam"] = "1992-10-17T18:42:07";
        requester._responseHeaders["datetimeArrayParam"] = [ "1992-10-17T18:42:07", "1991-10-24T18:42:07" ];
        requester._responseHeaders["timeParam"] = "17:14:12";
        requester._responseHeaders["timeArrayParam"] = [ "17:14:12", "02:14:12" ];
        requester._responseHeaders["boolParam"] = "true";
        requester._responseHeaders["boolArrayParam"] = [ "true" ];

        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.SimpleResourcesAPIClient( requester, "http://gateway" );
        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.HeaderParamsGetRequestBuilder();
        request.uriParams( "myUriParam" );
        request.stringParam( "myStringParam" );
        request.stringArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestStringArrayParamList( "tata", "yoyo" )
        );
        request.intParam( 7 );
        request.intArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestIntArrayParamList( 1, 2 )
        );
        request.floatParam( 7.1 );
        request.floatArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestFloatArrayParamList( 1.3, 22.2 )
        );
        request.dateParam( new FlexDate( "1992-10-17" ) );
        request.dateArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestDateArrayParamList( new FlexDate( "1992-10-02" ), new FlexDate( "1992-10-17" ) )
        );
        request.datetimeParam( new FlexDateTime( "1992-10-17T14:12:07" ) );
        request.datetimeArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestDatetimeArrayParamList( new FlexDateTime( "1992-10-17T14:12:07" ), new FlexDateTime( "1992-10-02T13:00:00" ) )
        );
        request.timeParam( new FlexTime( "14:12:07" ) );
        request.timeArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestTimeArrayParamList( new FlexTime( "14:12:07" ), new FlexTime( "13:00:00" ) )
        );
        request.boolParam( true );
        request.boolArrayParam(
            new window[FLEXIO_IMPORT_OBJECT].org.generated.api.headerparamsgetrequest.HeaderParamsGetRequestBoolArrayParamList( true, true, false )
        );

        var response = client.headerParams().headerParamsGet( request.build() );

        assert.deepStrictEqual( response.status200().stringParam(), "resp-stringParam" );
        assert.deepStrictEqual( response.status200().stringArrayParam()[0], "v1" );
        assert.deepStrictEqual( response.status200().stringArrayParam()[1], "v2" );
        assert.deepStrictEqual( response.status200().intParam(), 7 );
        assert.deepStrictEqual( response.status200().intArrayParam()[0], 7 );
        assert.deepStrictEqual( response.status200().intArrayParam()[1], 8 );
        assert.deepStrictEqual( response.status200().floatParam(), 5.4 );
        assert.deepStrictEqual( response.status200().floatArrayParam()[0], 5.4 );
        assert.deepStrictEqual( response.status200().floatArrayParam()[1], 5.6 );
        assert.deepStrictEqual( response.status200().dateParam().toJSON(), "1993-10-17" );
        assert.deepStrictEqual( response.status200().dateArrayParam()[0].toJSON(), "1993-10-17" );
        assert.deepStrictEqual( response.status200().dateArrayParam()[1].toJSON(), "1992-10-17" );
        assert.deepStrictEqual( response.status200().datetimeParam().toJSON(), "1992-10-17T18:42:07" );
        assert.deepStrictEqual( response.status200().datetimeArrayParam()[0].toJSON(), "1992-10-17T18:42:07" );
        assert.deepStrictEqual( response.status200().datetimeArrayParam()[1].toJSON(), "1991-10-24T18:42:07" );
        assert.deepStrictEqual( response.status200().timeParam().toJSON(), "17:14:12" );
        assert.deepStrictEqual( response.status200().timeArrayParam()[0].toJSON(), "17:14:12" );
        assert.deepStrictEqual( response.status200().timeArrayParam()[1].toJSON(), "02:14:12" );
        assert.deepStrictEqual( response.status200().boolParam(), true );
        assert.deepStrictEqual( response.status200().boolArrayParam()[0], true );

        assert.deepStrictEqual( requester._headers["stringParam"], "myStringParam" );
        assert.deepStrictEqual( requester._headers["stringArrayParam"][0], "tata" );
        assert.deepStrictEqual( requester._headers["stringArrayParam"][1], "yoyo" );
        assert.deepStrictEqual( requester._headers["intParam"], "7" );
        assert.deepStrictEqual( requester._headers["intArrayParam"][0], "1" );
        assert.deepStrictEqual( requester._headers["intArrayParam"][1], "2" );
        assert.deepStrictEqual( requester._headers["floatParam"], "7.1" );
        assert.deepStrictEqual( requester._headers["floatArrayParam"][0], "1.3" );
        assert.deepStrictEqual( requester._headers["floatArrayParam"][1], "22.2" );
        assert.deepStrictEqual( requester._headers["dateParam"], "1992-10-17" );
        assert.deepStrictEqual( requester._headers["dateArrayParam"][0], "1992-10-02" );
        assert.deepStrictEqual( requester._headers["dateArrayParam"][1], "1992-10-17" );
        assert.deepStrictEqual( requester._headers["datetimeParam"], "1992-10-17T14:12:07" );
        assert.deepStrictEqual( requester._headers["datetimeArrayParam"][0], "1992-10-17T14:12:07" );
        assert.deepStrictEqual( requester._headers["datetimeArrayParam"][1], "1992-10-02T13:00:00" );
        assert.deepStrictEqual( requester._headers["timeParam"], "14:12:07" );
        assert.deepStrictEqual( requester._headers["timeArrayParam"][0], "14:12:07" );
        assert.deepStrictEqual( requester._headers["timeArrayParam"][1], "13:00:00" );
        assert.deepStrictEqual( requester._headers["boolParam"], "true" );
        assert.deepStrictEqual( requester._headers["boolArrayParam"][0], "true" );
        assert.deepStrictEqual( requester._headers["boolArrayParam"][1], "true" );
        assert.deepStrictEqual( requester._headers["boolArrayParam"][2], "false" );

        assert.deepStrictEqual( requester._path, "http://gateway/header-params/myUriParam" );
    }

    testDoubleUriParam(){
        var requester = new FakeHttpRequester();
        var client = new window[FLEXIO_IMPORT_OBJECT].org.generated.client.SimpleResourcesAPIClient( requester, "http://gateway" );
        var request = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.ParamsArrayGetRequestBuilder();
        var uriParams = new window[FLEXIO_IMPORT_OBJECT].org.generated.api.paramsarray.ParamsArrayUriParamsList( "p1", "p2" );
        request.uriParams( uriParams );
        var response = client.headerParams().paramsArray().paramsArrayGet( request.build() );

        assert.deepStrictEqual( requester._path, "http://gateway/header-params/p1/p2" );
    }

}

runTest( PayloadAndParameterTest );