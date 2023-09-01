package org.codingmatters.rest.undertow.internal;

import io.undertow.server.HttpServerExchange;
import org.codingmatters.rest.io.CountedReferenceTemporaryFile;
import org.codingmatters.rest.io.content.ContentHelper;
import org.codingmatters.rest.undertow.UndertowRequestDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CountedReferenceTemporaryFileRequestBody implements RequestBody {
    static private final Logger log = LoggerFactory.getLogger(UndertowRequestDelegate.class);

    static public RequestBody from(HttpServerExchange exchange) throws IOException {
        CountedReferenceTemporaryFile temp = null;
        temp = CountedReferenceTemporaryFile.create();
        try(OutputStream out = temp.outputStream()) {
            ContentHelper.copyStream(exchange.getInputStream(), out);
        }
        return new CountedReferenceTemporaryFileRequestBody(temp);
    }

    private final CountedReferenceTemporaryFile temp;

    public CountedReferenceTemporaryFileRequestBody(CountedReferenceTemporaryFile temp) {
        this.temp = temp;
    }

    @Override
    public InputStream inputStream() {
        try {
            return this.temp.inputStream();
        } catch (FileNotFoundException e) {
            log.error("", e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public void close() throws Exception {
        this.temp.close();
    }
}
