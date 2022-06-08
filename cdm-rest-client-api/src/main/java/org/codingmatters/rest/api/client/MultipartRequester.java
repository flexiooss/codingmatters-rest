package org.codingmatters.rest.api.client;

import org.codingmatters.rest.io.Content;

import java.io.File;
import java.io.IOException;

public interface MultipartRequester{

    @Deprecated
    public MultipartRequester formDataPart(String contentType, byte[] body, String name);
    public MultipartRequester formDataPart(String contentType, Content body, String name) throws IOException;
    public MultipartRequester formDataPart(String contentType, File file, String name) throws IOException;

    public ResponseDelegate post() throws IOException;
    public ResponseDelegate put() throws IOException;
    public ResponseDelegate patch() throws IOException;
}
