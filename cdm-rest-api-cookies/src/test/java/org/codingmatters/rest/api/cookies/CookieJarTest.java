package org.codingmatters.rest.api.cookies;

import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.tests.api.TestRequestDeleguate;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CookieJarTest {
    @Test
    public void whenNoCookieHeader__thenJarIsEmpty() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .build()
        );
        assertThat(jar.cookies(), is(emptyArray()));
    }
    @Test
    public void givenCookieHeader__whenHeaderIsEmpty__thenJarIsEmpty() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie").build()
                );
        assertThat(jar.cookies(), is(emptyArray()));
    }
    @Test
    public void givenCookieHeader__whenHeaderIsEmptyString__thenJarIsEmpty() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "").build()
                );
        assertThat(jar.cookies(), is(emptyArray()));
    }
    @Test
    public void givenCookieHeader__whenHeaderIsPairWithNoNameAndNoValue__thenJarIsEmpty() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "=").build()
                );
        assertThat(jar.cookies(), is(emptyArray()));
    }
    @Test
    public void givenCookieHeader__whenCookiePairValueIsEmpty__thenCookieDefined_andValueIsNull() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "name=").build());
        assertThat(jar.cookies(), is(arrayContaining("name")));
        assertThat(jar.cookie("name"), is(""));
    }
    @Test
    public void givenCookieHeader__whenCookieIsNotAPair__thenEmptyNameCookieDefined_andValueIsTheContent() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "content").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("")));
        assertThat(jar.cookie(""), is("content"));
    }
    @Test
    public void givenCookieHeader__whenCookieIsAPairWithEmptyName__thenEmptyNameCookieDefined_andValueIsTheContent() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "=content").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("")));
        assertThat(jar.cookie(""), is("content"));
    }
    @Test
    public void givenCookieHeader__whenCookieIsAPairWithEmptyName_andSpaces__thenEmptyNameCookieDefined_andValueIsTheTrimmedContent() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "= content ").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("")));
        assertThat(jar.cookie(""), is("content"));
    }
    @Test
    public void givenCookieHeader__whenCookieIsAValidPair__thenCookieAndValueAreDefined() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "name=value").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("name")));
        assertThat(jar.cookie("name"), is("value"));
    }
    @Test
    public void givenCookieHeader__whenCookieIsAValidPairWithSpaces__thenCookieAndValueAreDefinedAndTrimmed() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", " name = value ").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("name")));
        assertThat(jar.cookie("name"), is("value"));
    }
    @Test
    public void givenCookieHeader__whenCookieHasManyPairs__thenCookiesAndValuesAreDefined() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "name=value; name2=value2; name3=value3").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("name", "name2", "name3")));
        assertThat(jar.cookie("name"), is("value"));
        assertThat(jar.cookie("name2"), is("value2"));
        assertThat(jar.cookie("name3"), is("value3"));
    }
    @Test
    public void givenCookieHeader__whenMultiple__thenOnlyFirstAccounted() throws Exception {
        CookieJar jar = new CookieJar(TestRequestDeleguate.request(RequestDelegate.Method.POST, "https://some.where/over/the/rainbow")
                .addHeader("Cookie", "name=value", "name2=value2").build()
                );
        assertThat(jar.cookies(), is(arrayContaining("name")));
        assertThat(jar.cookie("name"), is("value"));
    }
}