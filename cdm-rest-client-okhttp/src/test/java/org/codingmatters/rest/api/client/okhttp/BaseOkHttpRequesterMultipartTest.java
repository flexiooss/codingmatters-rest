package org.codingmatters.rest.api.client.okhttp;

import okhttp3.*;
import org.codingmatters.rest.io.Content;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BaseOkHttpRequesterMultipartTest {

    AtomicReference<String> content = new AtomicReference<>();
    AtomicReference<String> method = new AtomicReference<>();
    AtomicReference<String> contentDisposition = new AtomicReference<>();
    AtomicReference<String> requestContentType = new AtomicReference<>();
    AtomicReference<String> partContentType = new AtomicReference<>();
    AtomicInteger partCount = new AtomicInteger();


    @Test
    public void givenAddTwoFormParts_whenPost_thenGet2Parts() throws IOException {
        final HttpClientWrapper client = new HttpClientWrapper() {
            @Override
            public Response execute(Request request) throws IOException {
                MultipartBody body = (MultipartBody) request.body();
                partCount.set(body.parts().size());
                return createFakeResponse();
            }
        };
        new BaseOkHttpRequester(client, ()->"https://eddeee")
                .multipart(MultipartBody.FORM)
                .formDataPart("ct1", Content.from("content 1"), "name 1")
                .formDataPart("ct2", Content.from("content 2"), "name 2")
                .postMultiPart();
        assertThat(partCount.get(), is(2));
    }

    @Test
    public void givenAddFileFormPart_whenPost_thenPartIsFile() throws IOException {
        final HttpClientWrapper client = new HttpClientWrapper() {
            @Override
            public Response execute(Request request) throws IOException {
                MultipartBody body = (MultipartBody) request.body();
                requestContentType.set(body.contentType().toString());
                contentDisposition.set(body.part(0).headers().get("Content-Disposition"));
                partContentType.set(body.part(0).body().contentType().toString());
                return createFakeResponse();
            }
        };
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("textFile.txt");
        File file = this.folder.newFile();
        Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        new BaseOkHttpRequester(client, () -> "http://my_test_url")
                .multipart(MultipartBody.FORM)
                .formDataPart("text/plain", file, "name 1")
                .postMultiPart();
        assertThat(requestContentType.get().startsWith("multipart/form-data; boundary="), is(true));
        assertThat(partContentType.get(), is("text/plain"));
        assertThat(contentDisposition.get().startsWith("form-data; name=\"name 1\"; filename=\"junit"), is(true));
    }

    @Test
    public void givenAddTextFormPart_whenPost_thenPartIsText() throws IOException {
        final HttpClientWrapper client = new HttpClientWrapper() {
            @Override
            public Response execute(Request request) throws IOException {
                MultipartBody body = (MultipartBody) request.body();
                requestContentType.set(body.contentType().toString());
                contentDisposition.set(body.part(0).headers().get("Content-Disposition"));
                content.set(body.part(0).body().toString());
                return createFakeResponse();
            }
        };
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("textFile.txt");
        File file = this.folder.newFile();
        Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        new BaseOkHttpRequester(client, () -> "http://my_test_url")
                .multipart(MultipartBody.FORM)
                .formDataPart(null, Content.from("Hello World of Multipart"), "name 1")
                .postMultiPart();
        assertThat(requestContentType.get().startsWith("multipart/form-data; boundary="), is(true));
        assertThat(contentDisposition.get().startsWith("form-data; name=\"name 1\""), is(true));
    }


    @Test
    public void givenFormPart_whenPost_thenClientPost() throws IOException {
        final HttpClientWrapper client = new HttpClientWrapper() {
            @Override
            public Response execute(Request request) throws IOException {
                MultipartBody body = (MultipartBody) request.body();
                method.set(request.method());
                return createFakeResponse();
            }
        };
        new BaseOkHttpRequester(client, ()->"https://eddeee")
                .multipart(MultipartBody.FORM)
                .formDataPart("ct1", Content.from("content 1"), "name 1")
                .formDataPart("ct2", Content.from("content 2"), "name 2")
                .postMultiPart();
        assertThat(method.get(), is("POST"));
    }

    @Test
    public void givenFormPart_whenPatch_thenClientPatch() throws IOException {
        final HttpClientWrapper client = new HttpClientWrapper() {
            @Override
            public Response execute(Request request) throws IOException {
                MultipartBody body = (MultipartBody) request.body();
                method.set(request.method());
                return createFakeResponse();
            }
        };
        new BaseOkHttpRequester(client, ()->"https://eddeee")
                .multipart(MultipartBody.FORM)
                .formDataPart("ct1", Content.from("content 1"), "name 1")
                .formDataPart("ct2", Content.from("content 2"), "name 2")
                .patchMultiPart();
        assertThat(method.get(), is("PATCH"));
    }

    @Test
    public void givenFormPart_whenPut_thenClientPut() throws IOException {
        final HttpClientWrapper client = new HttpClientWrapper() {
            @Override
            public Response execute(Request request) throws IOException {
                MultipartBody body = (MultipartBody) request.body();
                method.set(request.method());
                return createFakeResponse();
            }
        };
        new BaseOkHttpRequester(client, ()->"https://eddeee")
                .multipart(MultipartBody.FORM)
                .formDataPart("ct1", Content.from("content 1"), "name 1")
                .formDataPart("ct2", Content.from("content 2"), "name 2")
                .putMultiPart();
        assertThat(method.get(), is("PUT"));
    }

    private Response createFakeResponse() {
        return new Response.Builder()
                .code(200)
                .request(new Request.Builder().url("https://toto.com").build())
                .protocol(Protocol.HTTP_1_0)
                .message("Hello")
                .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                .build();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
}
