<?php

namespace org\utils;

use io\flexio\utils\http\ResponseDelegate;

class FakeResponseDelegate implements ResponseDelegate {

    private $headers;
    private $nextBody;

    public function __construct( string $nextBody, $headers = array() ){
        $this -> headers = $headers;
        $this-> nextBody = $nextBody;
    }

    public function addHeader( string $name, string $value ){
        $this->headers[$name] = $value;
    }

    public function code(): int {
        return 200;
    }

    public function body(): string {
        return $this-> nextBody;
    }

    public function header( string $name ): array {
        return $this -> headers[$name];
    }

    public function contentType(): string {
        return "";
    }

}