package org.codingmatters.rest.api;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
        TIMEONLY(DateTimeFormatter.ofPattern("HH:mm:ss[.SSS]['Z']")),
        DATETIMEONLY(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]['Z']"))
        ;

        public final DateTimeFormatter formatter;

        Formatters(DateTimeFormatter formatter) {
            this.formatter = formatter;
        }
    }
}
