package org.codingmatters.rest.api.generator.utils;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/6/17.
 */
public class NamingTest {

    @Test
    public void nullAndEmpty() throws Exception {
        assertThat(new Naming().type(null), Matchers.is(nullValue()));
        assertThat(new Naming().type(), Matchers.is(""));
        assertThat(new Naming().type(new String[0]), Matchers.is(""));

        assertThat(new Naming().property(null), Matchers.is(nullValue()));
        assertThat(new Naming().property(), Matchers.is(""));
        assertThat(new Naming().property(new String[0]), Matchers.is(""));
    }

    @Test
    public void camelCase() throws Exception {
        assertThat(new Naming().type("camel", "case"), is("CamelCase"));
        assertThat(new Naming().property("camel", "case"), is("camelCase"));
    }

    @Test
    public void dashesAreWordSeparators() throws Exception {
        assertThat(new Naming().type("camel-case"), is("CamelCase"));
        assertThat(new Naming().type("camel------case"), is("CamelCase"));
        assertThat(new Naming().property("camel-case"), is("camelCase"));
        assertThat(new Naming().property("camel-------case"), is("camelCase"));
    }

    @Test
    public void spacesAreWordSeparators() throws Exception {
        assertThat(new Naming().type("camel case"), is("CamelCase"));
        assertThat(new Naming().type("camel      case"), is("CamelCase"));
        assertThat(new Naming().property("camel case"), is("camelCase"));
        assertThat(new Naming().property("camel       case"), is("camelCase"));
    }
}