<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\utils\FakeHttpRequester;
use org\generated\api\RootImpl;
use org\generated\api\RootGetRequest;
use org\generated\api\RootPostRequest;
use org\generated\api\SubGetRequest;

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

    public function testSubPost(){
        $requester = new FakeHttpRequester();

        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $root = new \org\generated\types\RootObj();
        $root -> withTiti( "titi" )
              -> withGrosminet( 9 );

        $request = new \org\generated\api\SubPostRequest();
        $request -> withPayload( $root );

        $response = $rootImpl -> sub() -> subPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root/sub' );
        $this-> assertSame( $requester->lastMethod(), 'post' );

    }

}