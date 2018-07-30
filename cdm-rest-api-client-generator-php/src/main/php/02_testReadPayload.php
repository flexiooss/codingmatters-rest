<?php

namespace Test;

use PHPUnit\Framework\TestCase;

use org\utils\FakeHttpRequester;
use org\generated\api\RootImpl;
use org\generated\api\RootGetRequest;
use org\generated\api\RootPostRequest;
use org\generated\api\SubGetRequest;
use org\generated\types\RootObj;

class PayloadTest extends TestCase {

    public function testTypeRequestPayload() {
        $requester = new FakeHttpRequester();
        $rootImpl = new RootImpl( $requester, 'http://gateway.io/services' );
        $request = new RootPatchRequest();
        $request-> withPayload( getRootObj() );
    }

    public function testTypeArrayRequestPayload() {

    }

    public function testObjectRequestPayload() {

    }

    public function testObjectArrayRequestPayload() {

    }

    public function testFileRequestPayload() {

    }

    public function getRootObj() {
        $root = new \org\generated\types\RootObj();
        $root -> withTiti( "titi" )
          -> withGrosminet( 9 )
          -> withDay( \org\generated\types\rootobj\RootObjDay::LUNDI() )
          -> withDate( \io\flexio\utils\FlexDate::newDate( "2018-09-09" ) )
          -> withLittle( $little )
          -> withList( $list )
          ;
        return $root;
    }
}