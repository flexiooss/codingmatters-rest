package org.codingmatters.rest.api.cookies;

import org.codingmatters.rest.tests.api.TestResponseDeleguate;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CookieSetterTest {

    private final TestResponseDeleguate response = new TestResponseDeleguate();
    private final CookieSetter cookieSetter = new CookieSetter();

    @Test
    public void givenResponseWithoutSetCookie__whenNoCookieSet__thenSetCookieHeaderIsNull() throws Exception {
        this.cookieSetter.to(this.response);

        assertThat(this.response.headers().get("Set-Cookie"), is(nullValue()));
    }

    @Test
    public void givenResponseWithSetCookie__whenNoCookieSet__thenSetCookieLeftAsIs() throws Exception {
        this.cookieSetter.to(this.response.addHeader("Set-Cookie", "previous", "value"));

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("previous", "value")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenOneSimpleCookieSet__thenSetCookieHeaderHasValuePair() throws Exception {
        this.cookieSetter.set("name", "value").to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithExpiredAt__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .expires(LocalDateTime.of(2015, 10, 21, 7, 28, 0))
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Expires=Wed, 21 Oct 2015 07:28:00 GMT")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithMaxAge__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .maxAge(123456789L)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Max-Age=123456789")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithDomain__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .domain("www.some.where")
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Domain=www.some.where")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithPath__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .path("/docs/Web/")
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Path=/docs/Web/")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithSecureToTrue__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .secure(true)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Secure")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithSecureToFalse__thenAttributeNotAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .secure(false)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithHttpOnlyToTrue__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .httpOnly(true)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; HttpOnly")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithHttpOnlyToFalse__thenAttributeNotAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .httpOnly(false)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithSameSiteToNone__thenAttributeAppended_andSecureIsActivated() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .sameSite(CookieAttributes.SameSite.None)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Secure; SameSite=None")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithSameSiteToLax__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .sameSite(CookieAttributes.SameSite.Lax)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; SameSite=Lax")));
    }

    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithSameSiteToStrict__thenAttributeAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .sameSite(CookieAttributes.SameSite.Strict)
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; SameSite=Strict")));
    }



    @Test
    public void givenResponseWithoutSetCookie__whenCookieSetWithManyAttributes__thenAttributesAreAppended() throws Exception {
        this.cookieSetter.set("name", "value", CookieAttributes.builder()
                .domain("www.some.where")
                .path("/over/the/rainbow")
                .build()
        ).to(response);

        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining("name=value; Domain=www.some.where; Path=/over/the/rainbow")));
    }


    @Ignore
    @Test
    public void givenResponseWithoutSetCookie__whenManySimpleCookieSet__thenManySetCookieHeaderHasManyValuePair() throws Exception {
        this.cookieSetter
                .set("name1", "value1")
                .set("name2", "value2")
                .set("name3", "value3")
                .to(response);

        for (String s : this.response.headers().get("Set-Cookie")) {
            System.out.println(s);
        }


        assertThat(this.response.headers().get("Set-Cookie"), is(arrayContaining(
                "name1=value1",
                "name2=value2",
                "name3=value3"
        )));
    }
}