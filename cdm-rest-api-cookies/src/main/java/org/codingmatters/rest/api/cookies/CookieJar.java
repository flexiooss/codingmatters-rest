package org.codingmatters.rest.api.cookies;

import org.codingmatters.rest.api.RequestDelegate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * https://httpwg.org/specs/rfc6265.html#cookie
 * https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-rfc6265bis-05#section-5.3
 */
public class CookieJar {
    private final Map<String, String> cookies = new LinkedHashMap<>();

    public CookieJar(RequestDelegate request) {
        if(request.headers().get("Cookie") != null && ! request.headers().get("Cookie").isEmpty()) {
            String cookieString = request.headers().get("Cookie").get(0);
            if (cookieString != null && ! cookieString.isEmpty()) {
                for (String cookiePair : cookieString.split("; ")) {
                    int eqIndex = cookiePair.indexOf('=');
                    if(eqIndex <= 0 && cookiePair.length() > 1) {
                        this.cookies.put("", cookiePair.substring(eqIndex + 1).trim());
                    } else if(eqIndex > 0) {
                        this.cookies.putIfAbsent(cookiePair.substring(0, eqIndex).trim(), cookiePair.substring(eqIndex + 1).trim());
                    }
                }
            }
        }
    }

    public String cookie(String name) {
        return this.cookies.get(name);
    }

    public String[] cookies() {
        return this.cookies.keySet().toArray(new String[0]);
    }

}
