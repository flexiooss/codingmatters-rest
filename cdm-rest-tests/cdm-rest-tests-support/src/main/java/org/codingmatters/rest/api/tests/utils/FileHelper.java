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

    public void printFile(File root, String name) throws IOException {
        if(root.getName().equals(name)) {
            System.out.println("FILE CONTENT - " + root.getAbsolutePath());
            try(InputStream in = new FileInputStream(root) ; Reader reader = new InputStreamReader(in)) {
                char [] buffer = new char[1024];
                StringBuilder content = new StringBuilder();
                for(int read  = reader.read(buffer) ; read != -1 ; read = reader.read(buffer)) {
                    content.append(buffer, 0, read);
                }
                System.out.println(content);
            }
            System.out.println("--------------------------------");
        } else if(root.listFiles() != null) {
            for (File file : root.listFiles()) {
                this.printFile(file, name);
            }
        }
    }

    public void printJavaContent(String prefix, File root) {
        if(root.isDirectory()) {
            System.out.println(prefix + " + " + root.getName());
            for (File file : root.listFiles()) {
                this.printJavaContent(prefix + "   ", file);
            }
        } else if(root.getName().endsWith(".java")){
            System.out.println(prefix + "   " + root.getName());
        }
    }
}
