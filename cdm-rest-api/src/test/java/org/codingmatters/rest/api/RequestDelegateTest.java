package org.codingmatters.rest.api;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequestDelegateTest {

    public static final List<String> LIST_VALUE = Arrays.asList("yop");
    private Map<String, List<String>> headers = RequestDelegate.createHeaderMap();

    @Test
    public void givenPutWithLowerCase__whenGetWithLowerCase__thenValueGetted() throws Exception {
        headers.put("abc", LIST_VALUE);

        assertThat(headers.get("abc"), is(LIST_VALUE));
    }
    @Test
    public void givenPutWithLowerCase__whenGetWithUpperCase__thenValueGetted() throws Exception {
        headers.put("abc", LIST_VALUE);

        assertThat(headers.get("ABC"), is(LIST_VALUE));
    }
    @Test
    public void givenPutWithUpperCase__whenGetWithUpperCase__thenValueGetted() throws Exception {
        headers.put("ABC", LIST_VALUE);

        assertThat(headers.get("ABC"), is(LIST_VALUE));
    }
    @Test
    public void givenPutWithUpperCase__whenGetWithLowerCase__thenValueGetted() throws Exception {
        headers.put("ABC", LIST_VALUE);

        assertThat(headers.get("abc"), is(LIST_VALUE));
    }
}