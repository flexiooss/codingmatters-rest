package org.codingmatters.rest.api.cookies;

import org.codingmatters.rest.api.ResponseDelegate;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * https://developer.mozilla.org/fr/docs/Web/HTTP/Headers/Set-Cookie#voir_aussi
 * https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-rfc6265bis-05
 */
public class CookieSetter {

    private final List<ToSetCookie> toSet = new LinkedList<>();

    public CookieSetter() {
    }

    public CookieSetter set(String name, String value, CookieAttributes attributes) {
        this.toSet.add(new ToSetCookie(name, value, attributes != null ? attributes : CookieAttributes.builder().build()));
        return this;
    }
    public CookieSetter set(String name, String value) {
        return this.set(name, value, null);
    }

    public void to(ResponseDelegate response) {
        for (ToSetCookie cookie : this.toSet) {
            response.addHeader("Set-Cookie", cookie.formatted());
        }
    }

    public String[] cookieValues() {
        return this.toSet.stream().map(toSetCookie -> toSetCookie.formatted()).toArray(i -> new String[i]);
    }

    private class ToSetCookie {
        public final String name;
        public final String value;
        public final CookieAttributes attributes;

        public ToSetCookie(String name, String value, CookieAttributes attributes) {
            this.name = name;
            this.value = value;
            if(attributes.opt().sameSite().isPresent() && attributes.sameSite().equals(CookieAttributes.SameSite.None)) {
                attributes = attributes.withSecure(true);
            }
            this.attributes = attributes;
        }

        public String formatted() {
            return String.format("%s=%s%s", this.name, this.value, this.formattedAttributes());
        }

        private Object formattedAttributes() {
            StringBuilder result = new StringBuilder();
            if(this.attributes.opt().expires().isPresent()) {
                result.append(String.format(Locale.ENGLISH,"; Expires=%1$ta, %1$td %1$tb %1$tY %1$tH:%1$tM:%1$tS GMT", this.attributes.expires()));
            }
            if(this.attributes.opt().maxAge().isPresent()) {
                result.append(String.format("; Max-Age=%d", this.attributes.maxAge()));
            }
            if(this.attributes.opt().domain().isPresent()) {
                result.append(String.format("; Domain=%s", this.attributes.domain()));
            }
            if(this.attributes.opt().path().isPresent()) {
                result.append(String.format("; Path=%s", this.attributes.path()));
            }
            if(this.attributes.opt().secure().isPresent() && this.attributes.secure()) {
                result.append("; Secure");
            }
            if(this.attributes.opt().httpOnly().isPresent() && this.attributes.httpOnly()) {
                result.append("; HttpOnly");
            }
            if(this.attributes.opt().sameSite().isPresent()) {
                result.append(String.format("; SameSite=%s", this.attributes.sameSite().name()));
            }
            return result.toString();
        }
    }
}
