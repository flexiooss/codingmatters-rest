package org.codingmatters.rest.io;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Encodings {
    interface Url {
        static String encode(String str, Charset charset) {
            return URLEncoder.encode(str, charset).replaceAll("\\+", "%20");
        }
        static String encode(String str) {
            return encode(str, CharSet.UTF_8);
        }

        static String decode(String str, Charset charset) {
            return URLDecoder.decode(str, charset);
        }
        static String decode(String str) {
            return decode(str, CharSet.UTF_8);
        }
    }

    interface CharSet {
        java.nio.charset.Charset UTF_8 = StandardCharsets.UTF_8;

        static Charset from(String name) throws NoSuchCharsetException {
            if(name == null || name.trim().isEmpty()) {
                throw new NoSuchCharsetException("empty charset doesn't exists");
            }
            try {
                return Charset.forName(name);
            } catch (IllegalArgumentException e) {
                throw new NoSuchCharsetException("no such charset : " + name, e);
            }
        }

        class NoSuchCharsetException extends Exception {
            public NoSuchCharsetException(String message) {
                super(message);
            }

            public NoSuchCharsetException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }
}
