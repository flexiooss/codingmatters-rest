<?php

namespace org\utils;

use io\flexio\utils\http\ResponseDelegate;

class FakeResponseDelegate implements ResponseDelegate {

    private $headers;

    public function __construct( $headers = array() ){
        $this -> headers = $headers;
    }

    public function addHeader( string $name, string $value ){
        $this->headers[$name] = $value;
    }

    public function code(): int {
        return 200;
    }

    public function body(): string {
        return "";
    }

    public function header( string $name ): string {
        return $this -> headers[$name];
    }

    public function contentType(): string {
        return "";
    }

}