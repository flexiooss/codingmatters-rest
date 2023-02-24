package org.codingmatters.rest.api;

import java.io.IOException;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

/**
 * Created by nelt on 4/27/17.
 */
public interface Processor {
    void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException;

    enum Variables {
        API_PATH("%API_PATH%");

        private final String token;

        Variables(String token) {
            this.token = token;
        }

        public String token() {
            return token;
        }
    }


    enum Formatters {
        DATEONLY(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        TIMEONLY(new DateTimeFormatterBuilder().parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_TIME).optionalStart().appendLiteral("Z")
                .toFormatter()
        ),
        DATETIMEONLY(new DateTimeFormatterBuilder().parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME).optionalStart().appendLiteral("Z")
            .toFormatter()
        )
        ;

        public final DateTimeFormatter formatter;

        Formatters(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }
    }
}
