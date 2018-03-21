package org.codingmatters.rest.api.internal;

import org.codingmatters.rest.api.RequestDelegate;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/27/17.
 */
public class RequestDelegateUriParameterTest {

    @Test
    public void noParameter() throws Exception {
        RequestDelegate deleguate = this.withPath("/start/param-value");
        Map<String, List<String>> parameters = deleguate.uriParameters("/blop/blop");

        System.out.println(parameters);

        assertThat(parameters.size(), is(0));
    }

    @Test
    public void noMatch() throws Exception {
        RequestDelegate deleguate = this.withPath("/start/param-value");
        Map<String, List<String>> parameters = deleguate.uriParameters("/blop/{param-name}");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), is(empty()));
    }

    @Test
    public void lastPart() throws Exception {
        RequestDelegate deleguate = this.withPath("/start/param-value");
        Map<String, List<String>> parameters = deleguate.uriParameters("/start/{param-name}");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), contains("param-value"));
    }

    @Test
    public void middlePart() throws Exception {
        RequestDelegate deleguate = this.withPath("/start/param-value/end");
        Map<String, List<String>> parameters = deleguate.uriParameters("/start/{param-name}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), contains("param-value"));
    }

    @Test
    public void startPart() throws Exception {
        RequestDelegate deleguate = this.withPath("/param-value/end");
        Map<String, List<String>> parameters = deleguate.uriParameters("/{param-name}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param-name"), contains("param-value"));
    }

    @Test
    public void manyParams() throws Exception {
        RequestDelegate deleguate = this.withPath("/start/param1-value/middle/param2-value/end");
        Map<String, List<String>> parameters = deleguate.uriParameters("/start/{param1-name}/middle/{param2-name}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(2));
        assertThat(parameters.get("param1-name"), contains("param1-value"));
        assertThat(parameters.get("param2-name"), contains("param2-value"));
    }

    @Test
    public void listParam() throws Exception {
        RequestDelegate deleguate = this.withPath("/start/param-value1/middle/param-value2/end");
        Map<String, List<String>> parameters = deleguate.uriParameters("/start/{param}/middle/{param}/end");

        System.out.println(parameters);

        assertThat(parameters.size(), is(1));
        assertThat(parameters.get("param"), contains("param-value1", "param-value2"));
    }

    private RequestDelegate withPath(String path) {
        return new TestRequestDelegate(path);
    }

    class TestRequestDelegate implements RequestDelegate {

        private final String path;

        TestRequestDelegate(String path) {
            this.path = path;
        }

        @Override
        public String path() {
            return this.path;
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
        public String contentType() {
            return null;
        }

        @Override
        public String absolutePath(String relative) {
            return null;
        }

        @Override
        public Map<String, List<String>> uriParameters(String pathExpression) {
            return new UriParameterProcessor(this).process(pathExpression);
        }

        @Override
        public Map<String, List<String>> queryParameters() {
            return null;
        }

        @Override
        public Map<String, List<String>> headers() {
            return null;
        }
    }
}