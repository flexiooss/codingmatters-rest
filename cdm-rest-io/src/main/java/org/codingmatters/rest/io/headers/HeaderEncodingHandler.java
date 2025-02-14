package org.codingmatters.rest.io.headers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HeaderEncodingHandler {

    public static boolean needEncoding( String value ) {
        if (value != null) {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c <= 31 && c != '\t' || c >= 127) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isEncoded(String value) {
        return value != null && value.startsWith("utf-8''");
    }

    public static String encodeHeader( String value ) {
        try {
            return "utf-8''" + URLEncoder.encode( value, "utf-8" );
        } catch( UnsupportedEncodingException e ){
            return value;
        }
    }

    public static String decodeHeader( String value ) {
        String[] parts = value.split( "'" );
        if( parts.length == 3 ){
            try {
                return URLDecoder.decode( parts[2], parts[0] );
            } catch( UnsupportedEncodingException e ){
                return value;
            }
        } else {
            return value;
        }
    }
}
