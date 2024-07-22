package org.codingmatters.rest.api.client;

import okhttp3.MediaType;
import org.codingmatters.rest.io.Content;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public interface Requester {
    default MultipartRequester multipart(MediaType form) throws IOException {
        throw new IOException();
    }

    ResponseDelegate get() throws IOException;

    ResponseDelegate head() throws IOException;

    /**
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     * @deprecated use post(String contentType, Content body) instead
     */
    @Deprecated
    ResponseDelegate post(String contentType, byte[] body) throws IOException;

    ResponseDelegate post(String contentType, Content body) throws IOException;

    /**
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     * @deprecated use put(String contentType, Content body)
     */
    @Deprecated
    ResponseDelegate put(String contentType, byte[] body) throws IOException;

    ResponseDelegate put(String contentType, Content body) throws IOException;

    /**
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     * @deprecated use patch(String contentType, Content body) instead
     */
    @Deprecated
    ResponseDelegate patch(String contentType, byte[] body) throws IOException;

    ResponseDelegate patch(String contentType, Content body) throws IOException;

    ResponseDelegate delete() throws IOException;

    /**
     * @param contentType
     * @param body
     * @return
     * @throws IOException
     * @deprecated use delete(String contentType, Content body) instead
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

    class MissingUriParameterException extends IOException {
        public MissingUriParameterException(String message) {
            super(message);
        }

        public MissingUriParameterException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
