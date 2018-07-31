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

}