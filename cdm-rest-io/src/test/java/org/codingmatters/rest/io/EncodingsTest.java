package org.codingmatters.rest.io;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EncodingsTest {
    @Test
    void whileUrl__givenEncoding__whenSpaces__thenReplacedByPercent20() throws Exception {
        assertThat(
                Encodings.Url.encode("     "),
                is("%20%20%20%20%20")
        );
    }
    @Test
    void whileUrl__givenEncoding__whenPluses__thenReplacedByPercent2B() throws Exception {
        assertThat(
                Encodings.Url.encode("+++++"),
                is("%2B%2B%2B%2B%2B")
        );
    }

    @Test
    void whileUrl__givenEncoding__whenNoSpecialCharacters__thenUrlEncodedWithUTF8Charset() throws Exception {
        assertThat(
                Encodings.Url.encode("azertyuiop&\"'(-_)=')"),
                is("azertyuiop%26%22%27%28-_%29%3D%27%29")
        );
    }

    @Test
    void whileUrl__givenEncoding__whenAccentCharacters__thenUrlEncodedWithUTF8Charset() throws Exception {
        assertThat(
                Encodings.Url.encode("éèçàîë"),
                is("%C3%A9%C3%A8%C3%A7%C3%A0%C3%AE%C3%AB")
        );
    }
}