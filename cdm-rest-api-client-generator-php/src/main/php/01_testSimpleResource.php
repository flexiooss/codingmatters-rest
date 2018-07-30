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

    public function testRequestPayload(){
        $requester = new FakeHttpRequester();
        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $little = new \org\generated\types\LittleObject();
        $little -> withName( "do" );

        $list = new \org\generated\types\rootobj\RootObjListList();
        $list[] = $little;

        $root = new \org\generated\types\RootObj();
        $root -> withTiti( "titi" )
              -> withGrosminet( 9 )
              -> withDay( \org\generated\types\rootobj\RootObjDay::LUNDI() )
              -> withDate( \io\flexio\utils\FlexDate::newDate( "2018-09-09" ) )
              -> withLittle( $little )
              -> withList( $list )
              ;

        $request = new \org\generated\api\SubPostRequest();
        $request -> withPayload( $root );

        $response = $rootImpl -> sub() -> subPost( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root/sub' );
        $this-> assertSame( $requester->lastMethod(), 'post' );
        $this -> assertSame( $requester->lastBody(), '{"titi":"titi","grosminet":9,"day":"LUNDI","date":"2018-09-09","little":{"name":"do"},"list":[{"name":"do"}]}' );
    }

    public function testRequestQueryParameters() {
        $requester = new FakeHttpRequester();
        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $request = new \org\generated\api\QueryMeGetRequest();
        $request -> withToto( "foo" )
                -> withTiti( "bar" );

        $response = $rootImpl -> queryMe() ->  queryMeGet( $request );
        
        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root/query/me' );
        $this-> assertSame( $requester->lastMethod(), 'get' );
        $this -> assertSame( $requester -> lastParameters()['toto'], "foo" );
        $this -> assertSame( $requester -> lastParameters()['titi'], "bar" );
    }

    public function testRequestUriParameters() {
        $requester = new FakeHttpRequester();
        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $request = new \org\generated\api\UriParamPatchRequest();
        $request -> withParam( "foo" );

        $response = $rootImpl -> uriParam() ->  uriParamPatch( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root/uri/foo/here' );
        $this-> assertSame( $requester->lastMethod(), 'patch' );
    }

    public function testRequestHeaders() {
        $requester = new FakeHttpRequester();
        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );

        $request = new \org\generated\api\HeadedQueryPutRequest();
        $request -> withXName( "account" );

        $response = $rootImpl -> HeadedQuery() ->  headedQueryPut( $request );

        $this-> assertSame( $requester->getPath(), 'http://gateway.io/services/root/head/shot' );
        $this-> assertSame( $requester->lastMethod(), 'put' );
        $this -> assertSame( $requester -> lastHeaders()['x-name'], "account" );
    }
}