package org.codingmatters.rest.io.content;

import java.io.*;

public class ContentHelper {
    static public byte [] bytes(InputStream in) throws IOException {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            copyStream(in, out);
            return out.toByteArray();
        }
    }

    static public int toFile(InputStream in, File file) throws IOException {
        try(FileOutputStream out = new FileOutputStream(file)) {
            return copyStream(in, out);
        }
    }

    static public int copyStream(InputStream in, OutputStream out) throws IOException {
        int length = 0;

        byte [] buffer = new byte[1024];
        for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
            out.write(buffer, 0, read);
            length += read;
        }
        out.flush();
        out.close();

        return length;
    }
}
