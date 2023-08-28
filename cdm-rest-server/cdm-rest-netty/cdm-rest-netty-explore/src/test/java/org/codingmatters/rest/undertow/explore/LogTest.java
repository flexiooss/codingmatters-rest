package org.codingmatters.rest.undertow.explore;

import org.junit.Test;
import org.slf4j.LoggerFactory;

public class LogTest {

    @Test
    public void given__when__then() throws Exception {
        LoggerFactory.getLogger(LogTest.class).info("yop");
    }
}
