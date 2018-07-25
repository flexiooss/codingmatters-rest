package org.codingmatters.rest.php.api.client;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class Utils {

    public String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 ).toLowerCase( Locale.ENGLISH );
    }

    public String firstLetterLowerCase( String name ) {
        return name.substring( 0, 1 ).toLowerCase( Locale.ENGLISH ) + name.substring( 1 );
    }


    public String getJoinedName( String value ) {
        return String.join( "", Arrays.stream( value.split( "\\s" ) ).map( this::firstLetterUpperCase ).collect( Collectors.toList() ) );
    }
}
