<?php

namespace org\utils;


class FakeEnum implements \JsonSerializable {
    protected $value;

    private function __construct( $value ){
        $this->value = $value;
    }

    public function value(){
        return $this->value;
    }

    public static function __SINGLE(): FakeEnum {
        return new FakeEnum( 'SINGLE' );
    }

    public static function __MULTIPLE(): FakeEnum {
        return new FakeEnum( 'MULTIPLE' );
    }

    public static function valueOf( string $value ) {
        if( in_array($value, FakeEnum::values())){
            return new FakeEnum( $value );
        } else {
            return null;
        }
    }

    public static function values(){
        return array('SINGLE', 'MULTIPLE');
    }
    public function jsonSerialize() {
        return $this->value;
    }
}