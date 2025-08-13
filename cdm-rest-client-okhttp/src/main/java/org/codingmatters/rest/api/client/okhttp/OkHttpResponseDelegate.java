package org.codingmatters.rest.api.client.okhttp;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.codingmatters.rest.api.client.ResponseDelegate;
import org.codingmatters.rest.io.CountedReferenceTemporaryFile;
import org.codingmatters.rest.io.headers.HeaderEncodingHandler;

import java.io.*;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OkHttpResponseDelegate implements ResponseDelegate {
    private final int code;
    private final Map<String, List<String>> headers;
    private final String contentType;
    private final CountedReferenceTemporaryFile bodyFile;

    public OkHttpResponseDelegate(Response response) throws IOException {
        this.code = response.code();
        try(ResponseBody body = response.body()) {
            this.contentType = body.contentType() != null ? body.contentType().toString() : null;
            this.bodyFile = CountedReferenceTemporaryFile.create();

            try(OutputStream out = this.bodyFile.outputStream(); InputStream in = body.byteStream()) {
                byte [] buffer = new byte[1024];
                for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            }
        }
        this.headers = new HashMap<>(response.headers().toMultimap());
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public byte[] body() throws IOException {
        try(InputStream in = this.bodyFile.inputStream() ; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte [] buffer = new byte[1024];
            for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return out.toByteArray();
        }
    }

    @Override
    public InputStream bodyStream() throws IOException {
        return this.bodyFile.inputStream();
    }

    @Override
    public String[] header( String name ) {
        List<String> encodedHeaderValues = this.headers.getOrDefault( name.toLowerCase() + "*", Collections.emptyList() );
        List<String> headerValues = this.headers.getOrDefault( name.toLowerCase(), Collections.emptyList() );
        return headerValues.isEmpty() && encodedHeaderValues.isEmpty() ? null
                : Stream.concat( headerValues.stream().map(this::newDecodeValue), encodedHeaderValues.stream().map( this::decodeValue ) ).toArray( String[]::new );
    }

    private String newDecodeValue(String header) {
        if (HeaderEncodingHandler.isEncoded(header)) {
            return HeaderEncodingHandler.decodeHeader(header);
        } else {
            return header;
        }
    }

    private String decodeValue( String value ) {
        String[] parts = value.split( "'" );
        if( parts.length == 3 ){
            try {
                return URLDecoder.decode( parts[2], parts[0] );
            } catch( UnsupportedEncodingException e ){
                return value;
            }
        } else {
            return value;
        }
    }

    @Override
    public String[] headerNames() {
        return this.headers.keySet()
                .stream()
                .map(name -> {
                    if (name.endsWith("*")) {
                        return name.substring(0, name.length() - 1);
                    }
                    return name;
                })
                .distinct()
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    @Override
    public String[] rawHeaderNames() {
        return this.headers.keySet().toArray(new String[this.headers.size()]);
    }

    @Override
    public String contentType() {
        return this.contentType;
    }

    @Override
    public void close() throws Exception {
        this.bodyFile.close();
    }
}
