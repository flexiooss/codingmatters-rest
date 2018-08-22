package org.codingmatters.rest.api.client;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public interface Requester {
    ResponseDelegate get() throws IOException;
    ResponseDelegate head() throws IOException;
    ResponseDelegate post(String contentType, byte[] body) throws IOException;
    ResponseDelegate put(String contentType, byte[] body) throws IOException;
    ResponseDelegate patch(String contentType, byte[] body) throws IOException;
    ResponseDelegate delete() throws IOException;
    ResponseDelegate delete(String contentType, byte[] body) throws IOException;

    Requester parameter(String name, String value);
    Requester parameter(String name, String[] value);
    Requester parameter(String name, Iterable<String> value);

    Requester header(String name, String value);
    Requester header(String name, String[] value);
    Requester header(String name, Iterable<String> value);

    Requester path(String path);

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
