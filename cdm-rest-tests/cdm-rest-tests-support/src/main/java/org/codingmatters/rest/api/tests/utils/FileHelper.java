package org.codingmatters.rest.api.tests.utils;

import org.junit.rules.ExternalResource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by nelt on 5/2/17.
 */
public class FileHelper extends ExternalResource {

    private File temp;

    @Override
    protected void before() throws Throwable {
        this.temp = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        this.temp.mkdir();
    }

    @Override
    protected void after() {
        this.rm(this.temp);
    }

    private void rm(File file) {
        if(file.isDirectory()) {
            for (File child : file.listFiles()) {
                this.rm(child);
            }
        } else {
            file.delete();
        }
    }

    public File fileResource(String resource) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if(url.getProtocol().equals("file")) {
            return new File(url.toURI());
        } else {
            File result = new File(this.temp, resource);
            result.getParentFile().mkdirs();
            result.createNewFile();

            try(InputStream in = url.openStream(); OutputStream out = new FileOutputStream(result)) {
                byte [] buffer = new byte[1024];
                for(int read = in.read(buffer) ; read != -1 ; read = in.read(buffer)) {
                    out.write(buffer, 0, read);
                }
            }

            return result;
        }
    }
}
