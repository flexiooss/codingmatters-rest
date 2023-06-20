<?php

namespace org\utils;

use io\flexio\utils\TypedArray;

class FakeEnumList extends TypedArray {

     public function __construct( $input = array() ) {
         parent::__construct( function(  $item ){return $item; }, $input );
     }

     public function add( $item) {
         parent::append( $item );
     }
}