package org.codingmatters.rest.server.acceptance;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class ResponseDelegateAcceptanceTest extends BaseAcceptanceTest {

    private OkHttpClient client = new OkHttpClient();

    @Test
    public void contenType() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.contenType("yip/yop");});

        MediaType contentType = this.client.newCall(this.requestBuilder().get().build()).execute().body().contentType();
        assertThat(contentType.type(), is("yip"));
        assertThat(contentType.subtype(), is("yop"));
    }

    @Test
    public void status() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.status(201);});

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().code(), is(201));
    }

    @Test
    public void addHeader() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate
                .addHeader("yip", "yop")
                .addHeader( "encode", "kéké" );
        });

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().header("yip"), is("yop"));
        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().header("encode"), is("utf-8''k%C3%A9k%C3%A9"));
    }

    @Test
    public void givenHeaderSet__whenAddingIfNot__thenHeaderUntouched() throws Exception {

        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            responseDeleguate
                .addHeader("header", "existing value");
            responseDeleguate
                .addHeaderIfNot( "header", "new value" );
        });

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().header("header"), is("existing value"));
    }

    @Test
    public void givenHeaderNotSet__whenAddingIfNot__thenHeaderSet() throws Exception {

        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            responseDeleguate
                .addHeaderIfNot( "header", "new value" );
        });

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().header("header"), is("new value"));
    }

    @Test
    public void whenPayloadAsString() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.payload("yop yop", "utf-8");});

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().body().string(), is("yop yop"));
    }

    @Test
    public void whenPayloadAsBytes() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {responseDeleguate.payload("yop yop".getBytes("utf-8"));});

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().body().string(), is("yop yop"));
    }



    @Test
    public void whenPayloadAsStream() throws Exception {
        this.withProcessor((requestDeleguate, responseDeleguate) -> {
            try(InputStream in = new ByteArrayInputStream("yop yop".getBytes("utf-8"))) {
                responseDeleguate.payload(in);
            }
        });

        assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().body().string(), is("yop yop"));
    }

    @Test
    public void whenPayloadIsABigStream() throws Exception {
        File f = null;
        try {
            f = this.generateBigFile();
            File file = f;
            this.withProcessor((requestDeleguate, responseDeleguate) -> {
                try (InputStream in = new FileInputStream(file)) {
                    responseDeleguate.payload(in);
                }
            });

            assertThat(this.client.newCall(this.requestBuilder().get().build()).execute().body().string(), is(this.content(f)));
        } finally {
            if(f != null) {
                f.delete();
            }
        }
    }

    private String content(File file) throws IOException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream() ; InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            for(int read = in.read(buffer); read != -1 ; read = in.read(buffer)) {
                result.write(buffer, 0, read);
            }
            result.flush();
            result.close();
            return new String(result.toByteArray(), "utf-8");
        }
    }

    private File generateBigFile() throws IOException {
        File result = File.createTempFile("bif-file", ".txt");
        try(OutputStream out = new FileOutputStream(result)) {
            for (int i = 0; i < 100000; i++) {
                out.write("azertyuiopqsdfghjklmwxcvbn\n".getBytes("utf-8"));
            }
            out.flush();
        }
        return result;
    }
}
