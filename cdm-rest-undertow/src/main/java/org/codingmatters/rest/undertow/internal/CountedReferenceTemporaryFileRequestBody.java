package org.codingmatters.rest.undertow.internal;

import io.undertow.server.HttpServerExchange;
import org.codingmatters.rest.io.CountedReferenceTemporaryFile;
import org.codingmatters.rest.undertow.UndertowRequestDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CountedReferenceTemporaryFileRequestBody implements RequestBody {
    static private final Logger log = LoggerFactory.getLogger(UndertowRequestDelegate.class);

    static public RequestBody from(HttpServerExchange exchange) {
        CountedReferenceTemporaryFile temp = null;
        try {
            temp = CountedReferenceTemporaryFile.create();
        } catch (IOException e) {
            throw new RuntimeException("failed creating temporary file, cannot proceed in this condition", e);
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
