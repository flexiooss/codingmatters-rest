package org.codingmatters.rest.api.client.okhttp.exception;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class ConnectionTimeoutException extends IOException {
    public ConnectionTimeoutException(String msg, SocketTimeoutException cause) {
        super(msg, cause);
    }
}
