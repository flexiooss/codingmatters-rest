package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public interface HttpClientWrapper {
    Response execute(Request request) throws IOException;
}
