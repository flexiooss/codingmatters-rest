package org.codingmatters.http.api;

import org.junit.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/27/17.
 */
public class RequestDeleguateUriParameterTest {

    @Test
    public void noParameter() throws Exception {
        RequestDeleguate deleguate = this.withPath("/start/param-value");
        Map<String, String> parameters = deleguate.uriParameters("/blop/blop");

        System.out.println(parameters);

        assertThat(parameters.size(), is(0));
    }

    @Test
    public void noMatch() throws Exception {
        RequestDeleguate deleguate = this.withPath("/start/param-value");
        Map<String, String> parameters = deleguate.uriParameters("/blop/{param-name}");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), is(nullValue()));
    }

    @Test
    public void lastPart() throws Exception {
        RequestDeleguate deleguate = this.withPath("/start/param-value");
        Map<String, String> parameters = deleguate.uriParameters("/start/{param-name}");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), is("param-value"));
    }

    @Test
    public void middlePart() throws Exception {
        RequestDeleguate deleguate = this.withPath("/start/param-value/end");
        Map<String, String> parameters = deleguate.uriParameters("/start/{param-name}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), is("param-value"));
    }

    @Test
    public void startPart() throws Exception {
        RequestDeleguate deleguate = this.withPath("/param-value/end");
        Map<String, String> parameters = deleguate.uriParameters("/{param-name}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), is("param-value"));
    }

    @Test
    public void multiplePart() throws Exception {
        RequestDeleguate deleguate = this.withPath("/start/param1-value/middle/param2-value/end");
        Map<String, String> parameters = deleguate.uriParameters("/start/{param1-name}/middle/{param2-name}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(2));
        assertThat(parameters.get("param1-name"), is("param1-value"));
        assertThat(parameters.get("param2-name"), is("param2-value"));
    }

    private RequestDeleguate withPath(String path) {
        return new TestRequestDeleguate(path);
    }

    class TestRequestDeleguate implements RequestDeleguate {

        private final String path;

        TestRequestDeleguate(String path) {
            this.path = path;
        }

        @Override
        public Matcher pathMatcher(String regex) {
            return Pattern.compile(regex).matcher(this.path);
        }

        @Override
        public Method method() {
            return null;
        }

        @Override
        public InputStream payload() {
            return null;
        }

        @Override
        public String absolutePath(String relative) {
            return null;
        }
    }
}