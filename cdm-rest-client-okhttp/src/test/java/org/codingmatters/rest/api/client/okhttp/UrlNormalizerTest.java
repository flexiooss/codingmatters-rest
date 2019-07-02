package org.codingmatters.rest.api.client.okhttp;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class UrlNormalizerTest {

    @Test
    public void slashes() throws Exception {
        assertThat(new UrlNormalizer("http://some.where", "/down/here").normalize(), is("http://some.where/down/here"));
        assertThat(new UrlNormalizer("http://some.where/", "/down/here").normalize(), is("http://some.where/down/here"));
        assertThat(new UrlNormalizer("http://some.where", "/down/here/").normalize(), is("http://some.where/down/here"));
        assertThat(new UrlNormalizer("http://some.where", "/").normalize(), is("http://some.where"));
        assertThat(new UrlNormalizer("http://some.where/", "").normalize(), is("http://some.where"));
        assertThat(new UrlNormalizer("http://some.where/", "/").normalize(), is("http://some.where"));
    }
}