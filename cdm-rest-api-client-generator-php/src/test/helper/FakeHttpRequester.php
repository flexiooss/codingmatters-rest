<?php

namespace org\utils;

use io\flexio\utils\http\HttpRequester;
use io\flexio\utils\http\ResponseDelegate;

class FakeHttpRequester implements HttpRequester {

    private $path;
    private $lastMethod;
    private $parameters;
    private $headers;
    private $lastBody;
    private $nextBody;

    public function __construct() {
        $this -> parameters = array();
        $this -> headers = array();
        $this -> nextBody = "{}";
    }

    public function get(): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> nextBody, $this -> parameters );
        $this -> lastMethod = 'get';
        return $response;
    }

    public function post( string $contentType = null, string $body = null ): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> nextBody, $this -> parameters );
        $this -> lastMethod = 'post';
        $this -> lastBody = $body;
        return $response;
    }

    public function put( string $contentType = null, string $body = null ): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> nextBody, $this -> parameters );
        $this -> lastMethod = 'put';
        return $response;
    }

    public function patch( string $contentType = null, string $body = null ): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> nextBody, $this -> parameters );
        $this -> lastMethod = 'patch';
        return $response;
    }

    public function delete(): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> nextBody, $this -> parameters );
        $this -> lastMethod = 'delete';
        return $response;
    }

    public function head(): ResponseDelegate {
        $response = new FakeResponseDelegate( $this -> nextBody, $this -> parameters );
        $this -> lastMethod = 'head';
        return $response;
    }

    public function arrayParameter( string $name, array $values ) : HttpRequester {
        $this -> parameters[$name] = $values;
        return $this;
    }

    public function parameter( string $name, string $values ) : HttpRequester {
        $this -> parameters[$name] = array( $values );
        return $this;
    }

    public function arrayHeader( string $name, array $values ): HttpRequester {
        $this -> headers[$name] = $values;
        return $this;
    }

    public function header( string $name, string $values ): HttpRequester {
        $this -> headers[$name] = array( $values );
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

    public function lastBody(): string {
        return $this -> lastBody;
    }

    public function lastParameters() {
        return $this -> parameters;
    }

    public function lastHeaders(){
        return $this -> headers;
    }

    public function nextBody( string $body ){
        $this -> nextBody = $body;
    }
}
