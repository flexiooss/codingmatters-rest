package org.codingmatters.rest.api.client;

import org.codingmatters.rest.io.Content;
import org.codingmatters.rest.io.content.FileContent;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public interface Requester {
    ResponseDelegate get() throws IOException;
    ResponseDelegate head() throws IOException;

    /**
     * @deprecated use post(String contentType, Content body) instead
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     */
    @Deprecated
    ResponseDelegate post(String contentType, byte[] body) throws IOException;
    ResponseDelegate post(String contentType, Content body) throws IOException;

    /**
     * @deprecated use put(String contentType, Content body)
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     */
    @Deprecated
    ResponseDelegate put(String contentType, byte[] body) throws IOException;
    ResponseDelegate put(String contentType, Content body) throws IOException;

    /**
     * @deprecated use patch(String contentType, Content body) instead
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     */
    @Deprecated
    ResponseDelegate patch(String contentType, byte[] body) throws IOException;
    ResponseDelegate patch(String contentType, Content body) throws IOException;
    ResponseDelegate delete() throws IOException;

    /**
     * @deprecated use delete(String contentType, Content body) instead
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     */
    @Deprecated
    ResponseDelegate delete(String contentType, byte[] body) throws IOException;
    ResponseDelegate delete(String contentType, Content body) throws IOException;

    Requester parameter(String name, String value);
    Requester parameter(String name, String[] value);
    Requester parameter(String name, Iterable<String> value);

    Requester header(String name, String value);
    Requester header(String name, Iterable<String> value);
    Requester header(String name, String[] value);
    Requester headerIfNot(String name, String[] value);

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
