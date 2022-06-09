package org.codingmatters.rest.api.client;

import org.codingmatters.rest.io.Content;

import java.io.File;
import java.io.IOException;

public interface MultipartRequester{

    @Deprecated
    public MultipartRequester formDataPart(String contentType, byte[] body, String name);
    public MultipartRequester formDataPart(String contentType, Content body, String name) throws IOException;
    public MultipartRequester formDataPart(String contentType, File file, String name) throws IOException;

    public ResponseDelegate postMultiPart() throws IOException;
    public ResponseDelegate putMultiPart() throws IOException;
    public ResponseDelegate patchMultiPart() throws IOException;
}
