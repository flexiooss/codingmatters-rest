package org.codingmatters.poomjobs.http;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import io.undertow.Undertow;
import okhttp3.*;
import org.codingmatters.poomjobs.types.api.JobCollectionPostResponse;
import org.codingmatters.poomjobs.types.api.JobResourceGetResponse;
import org.codingmatters.poomjobs.types.api.JobResourcePutResponse;
import org.codingmatters.poomjobs.types.api.PoomjobsAPIHandlers;
import org.codingmatters.poomjobs.types.api.jobcollectionpostresponse.Status201;
import org.codingmatters.poomjobs.types.api.jobcollectionpostresponse.Status500;
import org.codingmatters.poomjobs.types.api.jobresourcegetresponse.Status200;
import org.codingmatters.poomjobs.types.api.jobresourcegetresponse.Status404;
import org.codingmatters.poomjobs.types.types.Error;
import org.codingmatters.poomjobs.types.types.Job;
import org.codingmatters.poomjobs.types.types.json.JobReader;
import org.codingmatters.poomjobs.types.types.json.JobWriter;
import org.codingmatters.rest.undertow.CdmHttpUndertowHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/25/17.
 */
public class PoomjobsAPIUndertowTest {

    private Undertow server;
    private OkHttpClient client = new OkHttpClient();
    private String baseUrl;
    private JsonFactory factory = new JsonFactory();

    @Before
    public void startUndertow() throws Exception {
        int port;
        try(ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }

        PoomjobsAPIHandlers handlers = PoomjobsAPIHandlers.Builder.builder()
                .jobCollectionPostHandler(
                        request -> {
                            if(request.payload().category().equals("failure")) {
                                return JobCollectionPostResponse.Builder.builder()
                                        .status500(Status500.Builder.builder()
                                                .payload(Error.Builder.builder()
                                                        .code("UNEXPECTED_ERROR")
                                                        .description("Unexpected error while adding job.")
                                                        .build())
                                                .build())
                                        .build();
                            } else {
                                return JobCollectionPostResponse.Builder.builder()
                                        .status201(Status201.Builder.builder()
                                                .location("/jobs/121212")
                                                .build())
                                        .build();
                            }
                        }
                )
                .jobResourceGetHandler(
                        request -> {
                            if(request.jobId().equals("12")) {
                                return JobResourceGetResponse.Builder.builder()
                                        .status200(Status200.Builder.builder()
                                                .payload(Job.Builder.builder()
                                                        .type("test")
                                                        .category("test")
                                                        .build())
                                                .build())
                                        .build();
                            } else if(request.jobId().equals("404")) {
                                return JobResourceGetResponse.Builder.builder()
                                        .status404(Status404.Builder.builder()
                                                .payload(Error.Builder.builder()
                                                        .code("JOB_NOT_FOUND")
                                                        .description("no job found with the given jobId")
                                                        .build())
                                                .build())
                                        .build();
                            } else {
                                return JobResourceGetResponse.Builder.builder()
                                        .status500(org.codingmatters.poomjobs.types.api.jobresourcegetresponse.Status500.Builder.builder()
                                                .payload(Error.Builder.builder()
                                                        .code("UNEXPECTED_ERROR")
                                                        .description("Unexpected error while getting job.")
                                                        .build())
                                                .build())
                                        .build();
                            }
                        }
                )
                .jobResourcePutHandler(
                        request -> {
                            if(request.jobId().equals("404")) {
                                return JobResourcePutResponse.Builder.builder()
                                        .status404(org.codingmatters.poomjobs.types.api.jobresourceputresponse.Status404.Builder.builder()
                                                .payload(Error.Builder.builder()
                                                        .code("JOB_NOT_FOUND")
                                                        .description("no job found with the given jobId")
                                                        .build())
                                                .build())
                                        .build();
                            } else if(request.payload().category().equals("failure")) {
                                return JobResourcePutResponse.Builder.builder()
                                        .status500(org.codingmatters.poomjobs.types.api.jobresourceputresponse.Status500.Builder.builder()
                                                .payload(Error.Builder.builder()
                                                        .code("UNEXPECTED_ERROR")
                                                        .description("Unexpected error while modifying job.")
                                                        .build())
                                                .build())
                                        .build();
                            } else {
                                return JobResourcePutResponse.Builder.builder()
                                        .status200(org.codingmatters.poomjobs.types.api.jobresourceputresponse.Status200.Builder.builder()
                                                .payload(request.payload())
                                                .build())
                                        .build();
                            }
                        }
                )
                .build();

        this.server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(new CdmHttpUndertowHandler(new PoomjobsAPIProcessor("poomjobs", new JsonFactory(), handlers))).build();
        this.server.start();

        this.baseUrl = "http://localhost:" + port;
    }

    @After
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void postJob_201() throws Exception {
        byte[] json;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            new JobWriter().write(
                    generator,
                    Job.Builder.builder()
                            .category("test")
                            .type("test")
                            .build()
            );
            generator.close();
            json = out.toByteArray();
        }
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build())
                .execute();

        assertThat(response.code(), is(201));
        assertThat(response.header("Location"), is(this.baseUrl + "/poomjobs/jobs/121212"));
    }


    @Test
    public void postJob_500() throws Exception {
        byte[] json;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            new JobWriter().write(
                    generator,
                    Job.Builder.builder()
                            .category("failure")
                            .type("test")
                            .build()
            );
            generator.close();
            json = out.toByteArray();
        }
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build())
                .execute();

        assertThat(response.code(), is(500));
        assertThat(response.body().string(), is("{\"code\":\"UNEXPECTED_ERROR\",\"description\":\"Unexpected error while adding job.\"}"));
    }

    @Test
    public void getJob_200() throws Exception {
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/12")
                .get()
                .build())
                .execute();

        assertThat(response.code(), is(200));
        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));

        try(JsonParser parser = factory.createParser(response.body().bytes())) {
            Job job = new JobReader().read(parser);
            assertThat(
                    job,
                    is(Job.Builder.builder()
                            .category("test")
                            .type("test")
                            .build())
            );
        }
    }

    @Test
    public void getJob_404() throws Exception {
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/404")
                .get()
                .build())
                .execute();
        assertThat(response.code(), is(404));
        assertThat(response.body().string(), is("{\"code\":\"JOB_NOT_FOUND\",\"description\":\"no job found with the given jobId\"}"));
    }

    @Test
    public void getJob_500() throws Exception {
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/500")
                .get()
                .build())
                .execute();
        assertThat(response.code(), is(500));
        assertThat(response.body().string(), is("{\"code\":\"UNEXPECTED_ERROR\",\"description\":\"Unexpected error while getting job.\"}"));
    }

    @Test
    public void putJob_200() throws Exception {
        byte[] json;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            new JobWriter().write(
                    generator,
                    Job.Builder.builder()
                            .category("test")
                            .type("changed")
                            .build()
            );
            generator.close();
            json = out.toByteArray();
        }
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/12")
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build())
                .execute();

        assertThat(response.code(), is(200));

        try(JsonParser parser = factory.createParser(response.body().bytes())) {
            Job job = new JobReader().read(parser);
            assertThat(
                    job,
                    is(Job.Builder.builder()
                            .category("test")
                            .type("changed")
                            .build())
            );
        }
    }

    @Test
    public void putJob_404() throws Exception {
        byte[] json;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            new JobWriter().write(
                    generator,
                    Job.Builder.builder()
                            .category("test")
                            .type("changed")
                            .build()
            );
            generator.close();
            json = out.toByteArray();
        }
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/404")
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build())
                .execute();

        assertThat(response.code(), is(404));
        assertThat(response.body().string(), is("{\"code\":\"JOB_NOT_FOUND\",\"description\":\"no job found with the given jobId\"}"));
    }

    @Test
    public void putJob_500() throws Exception {
        byte[] json;
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            JsonGenerator generator = this.factory.createGenerator(out);
            new JobWriter().write(
                    generator,
                    Job.Builder.builder()
                            .category("failure")
                            .type("changed")
                            .build()
            );
            generator.close();
            json = out.toByteArray();
        }
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/12")
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build())
                .execute();

        assertThat(response.code(), is(500));
        assertThat(response.body().string(), is("{\"code\":\"UNEXPECTED_ERROR\",\"description\":\"Unexpected error while modifying job.\"}"));
    }


}
