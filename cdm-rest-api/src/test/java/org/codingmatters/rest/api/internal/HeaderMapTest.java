package org.codingmatters.rest.api.internal;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;


public class HeaderMapTest {

    @Test
    public void givenHeaderStoredInLowerCase__whenGettingInLowerCase__thenValueRetrieved() throws Exception {
        HeaderMap actual = new HeaderMap();
        actual.put("key", Arrays.asList("value"));

        assertThat(actual.get("key"), contains("value"));
    }

    @Test
    public void givenHeaderStoredInLowerCase__whenGettingInUpperCase__thenValueRetrieved() throws Exception {
        HeaderMap actual = new HeaderMap();
        actual.put("key", Arrays.asList("value"));

        assertThat(actual.get("KEY"), contains("value"));
    }

    @Test
    public void givenHeaderStoredInUpperCase__whenGettingInLowerCase__thenValueRetrieved() throws Exception {
        HeaderMap actual = new HeaderMap();
        actual.put("KEY", Arrays.asList("value"));

        assertThat(actual.get("key"), contains("value"));
    }

    @Test
    public void givenHeaderStoredInUpperCase__whenGettingInUpperCase__thenValueRetrieved() throws Exception {
        HeaderMap actual = new HeaderMap();
        actual.put("KEY", Arrays.asList("value"));

        assertThat(actual.get("KEY"), contains("value"));
    }
}