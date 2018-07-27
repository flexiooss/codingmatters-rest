<?php

namespace org\utils;

use io\flexio\utils\http\HttpRequester;
use io\flexio\utils\http\ResponseDelegate;

class FakeHttpRequester implements HttpRequester {

    private $path;
    private $lastMethod;
    private $parameters;

    public function __construct() {
        $this -> parameters = array();
    }

    public function get(): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> parameters );
        $this -> lastMethod = 'get';
        return $response;
    }

    public function post( string $contentType = null, string $body = null ): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> parameters );
        $this -> lastMethod = 'post';
        return $response;
    }

    public function put( string $contentType = null, string $body = null ): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> parameters );
        $this -> lastMethod = 'put';
        return $response;
    }

    public function patch( string $contentType = null, string $body = null ): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> parameters );
        $this -> lastMethod = 'patch';
        return $response;
    }

    public function delete(): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> parameters );
        $this -> lastMethod = 'delete';
        return $response;
    }

    public function head(): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> parameters );
        $this -> lastMethod = 'head';
        return $response;
    }

    public function parameter( string $name, string $value ): HttpRequester {
        $this -> parameters[$name] = $value;
        return $this;
    }

    public function header( string $name, string $value ): HttpRequester {
        $this -> parameters[$name] = $value;
        return $this;
    }

    public function path( string $path ): HttpRequester {
        $this -> path = $path;
        return $this;
    }

    public function getPath(): string {
        return $this -> path;
    }

    public function lastMethod(): string {
        return $this -> lastMethod;
    }

}
