<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\utils\FakeHttpRequester;

class PayloadTest extends TestCase {

    public function getTestArray() {
        $array = array();

        $list = array();
        $list[] = "item1";
        $list[] = "item2";

        $array['toto'] = "foo";
        $array['joe'] = "bar";
        $array['list'] = $list;

        $subArray = array();
        $subArray["test"] = "sub";

        $array['sub'] = $subArray;
        return $array;
    }

    public function testObjectPayload() {
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\ObjectImpl( $requester, 'http://gateway' );
        $request = new \org\generated\api\ObjectPostRequest();

        $array = $this -> getTestArray();

        $request -> withPayload( $array );

        $requester -> nextBody('{"foo":"bar", "list":["foo", "bar"]}');
        $response = $client->objectPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway/object' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this-> assertSame( $requester->lastBody(), '{"toto":"foo","joe":"bar","list":["item1","item2"],"sub":{"test":"sub"}}' );

        $this -> assertNotNull( $response -> status200() );
        $this -> assertSame( $response -> status200() -> payload()["foo"], "bar" );
        $this -> assertSame( $response -> status200() -> payload()["list"][0], "foo" );
        $this -> assertSame( $response -> status200() -> payload()["list"][1], "bar" );
    }

    public function testObjectArrayPayload() {
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\ObjectArrayImpl( $requester, 'http://gateway' );
        $request = new \org\generated\api\ObjectArrayPostRequest();

        $array1 = $this -> getTestArray();
        $array2 = $this -> getTestArray();

        $list = new \org\generated\api\objectarraypostrequest\ObjectArrayPostRequestPayloadList();
        $list[] = $array1;
        $list[] = $array2;

        $request -> withPayload( $list );

        $requester -> nextBody('[{"foo":"bar","sub":{"test":"sub"}},{"bar":"foo", "list":["item1","item2"]}]');
        $response = $client->objectArrayPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway/objectArray' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this-> assertSame( $requester->lastBody(), '[{"toto":"foo","joe":"bar","list":["item1","item2"],"sub":{"test":"sub"}},{"toto":"foo","joe":"bar","list":["item1","item2"],"sub":{"test":"sub"}}]' );

        $this -> assertNotNull( $response -> status200() );
        $this -> assertSame( $response -> status200() -> payload()[0]["foo"], "bar" );
    }

    public function testObjectArrayShortSyntaxPayload() {
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\ObjectArrayShortImpl( $requester, 'http://gateway' );
        $request = new \org\generated\api\ObjectArrayShortPostRequest();

        $array1 = $this -> getTestArray();
        $array2 = $this -> getTestArray();

        $list = new \org\generated\api\objectarrayshortpostrequest\ObjectArrayShortPostRequestPayloadList();
        $list[] = $array1;
        $list[] = $array2;

        $request -> withPayload( $list );

        $requester -> nextBody('[{"foo":"bar","sub":{"test":"sub"}},{"bar":"foo", "list":["item1","item2"]}]');
        $response = $client->objectArrayShortPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway/objectArrayShort' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this-> assertSame( $requester->lastBody(), '[{"toto":"foo","joe":"bar","list":["item1","item2"],"sub":{"test":"sub"}},{"toto":"foo","joe":"bar","list":["item1","item2"],"sub":{"test":"sub"}}]' );

        $this -> assertNotNull( $response -> status200() );
        $this -> assertSame( $response -> status200() -> payload()[0]["foo"], "bar" );
    }

    public function testTypePayload(){
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\TypeImpl( $requester, 'http://gateway' );

        $payload = new \org\generated\types\LittleObject();
        $payload -> withName( "toto" );

        $request = new \org\generated\api\TypePostRequest();
        $request -> withPayload( $payload );

        $requester -> nextBody('{"name":"titi"}');
        $response = $client -> typePost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway/type' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this-> assertSame( $requester->lastBody(), '{"name":"toto"}' );

        $this -> assertSame( $response -> status200() -> payload()->name(), "titi" );
    }

    public function testTypeArrayPayload(){
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\TypeArrayImpl( $requester, 'http://gateway' );

        $item1 = new \org\generated\types\LittleObject();
        $item1 -> withName( "toto1" );
        $item2 = new \org\generated\types\LittleObject();
        $item2 -> withName( "toto2" );

        $payload = new \org\generated\api\typearraypostrequest\TypeArrayPostRequestPayloadList();
        $payload[] = $item1;
        $payload[] = $item2;

        $request = new \org\generated\api\TypeArrayPostRequest();
        $request -> withPayload( $payload );

        $requester -> nextBody('[{"name":"titi1"},{"name":"titi2"}]');
        $response = $client -> typeArrayPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway/typeArray' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this-> assertSame( $requester->lastBody(), '[{"name":"toto1"},{"name":"toto2"}]' );

        $this -> assertSame( $response -> status200() -> payload()[0]->name(), "titi1" );
        $this -> assertSame( $response -> status200() -> payload()[1]->name(), "titi2" );
    }

    public function testTypeArrayShortSyntaxPayload(){
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\TypeArrayShortImpl( $requester, 'http://gateway' );

        $item1 = new \org\generated\types\LittleObject();
        $item1 -> withName( "toto1" );
        $item2 = new \org\generated\types\LittleObject();
        $item2 -> withName( "toto2" );

        $payload = new \org\generated\api\typearrayshortpostrequest\TypeArrayShortPostRequestPayloadList();
        $payload[] = $item1;
        $payload[] = $item2;

        $request = new \org\generated\api\TypeArrayShortPostRequest();
        $request -> withPayload( $payload );

        $requester -> nextBody('[{"name":"titi1"},{"name":"titi2"}]');
        $response = $client -> typeArrayShortPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway/typeArrayShort' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this-> assertSame( $requester->lastBody(), '[{"name":"toto1"},{"name":"toto2"}]' );

        $this -> assertSame( $response -> status200() -> payload()[0]->name(), "titi1" );
        $this -> assertSame( $response -> status200() -> payload()[1]->name(), "titi2" );
    }

    public function testUriParametersHeadersAndQueryParams() {
        $requester = new FakeHttpRequester();
        $client = new \org\generated\api\ParametersImpl( $requester, 'http://gateway' );


        $strArray = new \org\generated\api\parametersgetrequest\ParametersGetRequestStrArrayList( array( "coucou1", "coucou2" ) );


        $request = new \org\generated\api\ParametersGetRequest();
        $request -> withFoo( "valFoo" )
                 -> withBar( "valBar" )
                 -> withStrHeader( "reqHeaderValue" )
                 -> withStrArray( $strArray )
                 -> withIntHeader( 7 )
                 -> withFloatHeader( 8 )
                 -> withDateHeader( \io\flexio\utils\FlexDate::newDate( '2011-08-01' ) )
                 -> withTimeHeader( \io\flexio\utils\FlexDate::newTime( '10:07:04' ) )
                 -> withDatetimeHeader( \io\flexio\utils\FlexDate::newDateTime( '2011-08-01T10:07:04' ) )
                 -> withBoolHeader( true )
                 -> withParams( "paramValue" )
                    ;

        $this-> assertSame( $request-> strHeader(), "reqHeaderValue" );
        $this-> assertSame( $request-> strArray()[0], "coucou1" );
        $this-> assertSame( $request-> strArray()[1], "coucou2" );
        $this-> assertSame( $request-> strHeader(), "reqHeaderValue" );
        $this-> assertSame( $request-> intHeader(), 7 );
        $this-> assertSame( $request-> floatHeader(), 8 );
        $this-> assertSame( $request-> dateHeader()->jsonSerialize(), "2011-08-01" );
        $this-> assertSame( $request-> timeHeader()->jsonSerialize(), "10:07:04" );
        $this-> assertSame( $request-> datetimeHeader()->jsonSerialize(), "2011-08-01T10:07:04" );
        $this-> assertSame( $request-> boolHeader(), true );

        $requester-> parameter( "str-header", "coucou" );
        $requester-> arrayParameter( "str-array", array( "coucou1", "coucou2" ) );
        $requester-> parameter( "int-header", "7" );
        $requester-> parameter( "float-header", "8" );
        $requester-> parameter( "date-header", "2011-08-01" );
        $requester-> arrayParameter( "date-array", array( "2011-08-01", "2011-08-02", "2011-08-03" ) );
        $requester-> parameter( "time-header", "10:07:04" );
        $requester-> parameter( "datetime-header", "2011-08-01T10:07:04" );
        $requester-> parameter( "bool-header", "false" );
        $requester-> arrayParameter( "bool-array", array( "true", "false" ));

        $response = $client -> parametersGet( $request );
        $this-> assertSame( $requester->getPath(), 'http://gateway/params/paramValue' );
        $this-> assertSame( $requester->lastMethod(), 'get' );
        $this-> assertSame( $requester->lastHeaders()['str-header'][0], "reqHeaderValue" );
        $this-> assertSame( $requester->lastHeaders()['int-header'][0], '7' );
        $this-> assertSame( $requester->lastHeaders()['float-header'][0], '8' );
        $this-> assertSame( $requester->lastHeaders()['date-header'][0], '2011-08-01' );
        $this-> assertSame( $requester->lastHeaders()['time-header'][0], '10:07:04' );
        $this-> assertSame( $requester->lastHeaders()['datetime-header'][0], '2011-08-01T10:07:04' );
        $this-> assertSame( $requester->lastHeaders()['bool-header'][0], 'true' );

        $this-> assertSame( $requester -> lastParameters()['foo'][0], "valFoo" );
        $this-> assertSame( $requester -> lastParameters()['bar'][0], "valBar" );
        $this-> assertSame( $response -> status200() -> strHeader(), "coucou" );
        $this-> assertSame( $response -> status200() -> strArray()[0], "coucou1" );
        $this-> assertSame( $response -> status200() -> strArray()[1], "coucou2" );
        $this-> assertSame( $response -> status200() -> intHeader(), 7 );
        $this-> assertSame( $response -> status200() -> floatHeader(), 8 );
        $this-> assertSame( $response -> status200() -> dateHeader()->jsonSerialize(), "2011-08-01" );
        $this-> assertSame( $response -> status200() -> dateArray()[0]->jsonSerialize(), "2011-08-01" );
        $this-> assertSame( $response -> status200() -> dateArray()[1]->jsonSerialize(), "2011-08-02" );
        $this-> assertSame( $response -> status200() -> dateArray()[2]->jsonSerialize(), "2011-08-03" );
        $this-> assertSame( $response -> status200() -> timeHeader()->jsonSerialize(), "10:07:04" );
        $this-> assertSame( $response -> status200() -> datetimeHeader()->jsonSerialize(), "2011-08-01T10:07:04" );
        $this-> assertSame( $response -> status200() -> boolHeader(), false );
        $this-> assertSame( $response -> status200() -> boolArray()[0], true );
        $this-> assertSame( $response -> status200() -> boolArray()[1], false );
    }

}