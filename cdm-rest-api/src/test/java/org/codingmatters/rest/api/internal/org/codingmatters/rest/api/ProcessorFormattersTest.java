package org.codingmatters.rest.api.internal.org.codingmatters.rest.api;

import org.codingmatters.rest.api.Processor;
import org.junit.Test;

import java.time.LocalDateTime;

public class ProcessorFormattersTest {

    @Test
    public void dateTimeOnly() {
        LocalDateTime.parse("2018-12-25T23:25:45");
        LocalDateTime.parse("2018-12-25T23:25:45.456");

        Processor.Formatters.DATETIMEONLY.formatter.parse("2018-12-25T23:54:25.123Z");
        Processor.Formatters.DATETIMEONLY.formatter.parse("2018-12-25T23:54:25Z");
        Processor.Formatters.DATETIMEONLY.formatter.parse("2018-12-25T23:54:25.123Z");
        Processor.Formatters.DATETIMEONLY.formatter.parse("2018-12-25T23:54:25");
    }
}
