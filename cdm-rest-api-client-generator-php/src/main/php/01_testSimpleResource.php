<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\utils\FakeHttpRequester;
use org\generated\RootImpl;
use org\generated\RootGetRequest;
use org\generated\RootPostRequest;
use org\generated\SubGetRequest;

class SimpleResourceTest extends TestCase {

    public function testRootGet(){
        $requester = new FakeHttpRequester();

        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $response = $rootImpl -> rootGet( new RootGetRequest() );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root' );
        $this-> assertSame( $requester->lastMethod(), 'get' );
    }

    public function testRootPost(){
        $requester = new FakeHttpRequester();

        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $response = $rootImpl -> rootPost( new RootPostRequest() );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
    }


    public function testSubGet(){
        $requester = new FakeHttpRequester();

        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $response = $rootImpl -> sub() -> subGet( new SubGetRequest() );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root/sub' );
        $this-> assertSame( $requester->lastMethod(), 'get' );
    }

}