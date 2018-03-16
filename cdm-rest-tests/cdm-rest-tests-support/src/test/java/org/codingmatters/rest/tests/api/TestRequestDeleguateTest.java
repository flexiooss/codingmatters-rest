package org.codingmatters.rest.tests.api;

import org.codingmatters.rest.api.RequestDelegate;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TestRequestDeleguateTest {

    @Test
    public void absolutePath() {
        assertThat(
                TestRequestDeleguate.request(RequestDelegate.Method.GET, "http://localhost:5689/a/long/path").build()
                        .absolutePath("//////yop/yop/taga/da"),
                is("http://localhost:5689/yop/yop/taga/da")
        );
        assertThat(
                TestRequestDeleguate.request(RequestDelegate.Method.GET, "http://localhost:5689").build()
                        .absolutePath("/yop/yop/taga/da"),
                is("http://localhost:5689/yop/yop/taga/da")
        );
        assertThat(
                TestRequestDeleguate.request(RequestDelegate.Method.GET, "http://localhost/a/long/path").build()
                        .absolutePath("/yop/yop/taga/da"),
                is("http://localhost/yop/yop/taga/da")
        );
        assertThat(
                TestRequestDeleguate.request(RequestDelegate.Method.GET, "https://localhost/a/long/path").build()
                        .absolutePath("/yop/yop/taga/da"),
                is("https://localhost/yop/yop/taga/da")
        );


    }
}