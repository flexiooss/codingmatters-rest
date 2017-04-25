package org.codingmatters.poomjobs.http;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.codingmatters.poomjobs.types.api.*;
import org.codingmatters.poomjobs.types.api.jobcollectionpostresponse.Status201;
import org.codingmatters.poomjobs.types.api.jobresourcegetresponse.Status200;
import org.codingmatters.poomjobs.types.types.Job;
import org.codingmatters.poomjobs.types.types.json.ErrorWriter;
import org.codingmatters.poomjobs.types.types.json.JobReader;
import org.codingmatters.poomjobs.types.types.json.JobWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/25/17.
 */
public class UndertowExploreTest {

    private Undertow server;
    private OkHttpClient client = new OkHttpClient();
    private String baseUrl;

    @Before
    public void startUndertow() throws Exception {
        int port;
        try(ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }

        PoomjobsAPIHandlers handlers = PoomjobsAPIHandlers.Builder.builder()
                .jobCollectionPostHandler(
                        request -> JobCollectionPostResponse.Builder.builder()
                                .status201(Status201.Builder.builder()
                                        .location("/poomjobs/jobs/121212")
                                        .build())
                                .build()
                )
                .jobResourceGetHandler(
                        request -> JobResourceGetResponse.Builder.builder()
                                .status200(Status200.Builder.builder()
                                        .payload(Job.Builder.builder()
                                                .type("test")
                                                .category("test")
                                                .build())
                                        .build())
                                .build()
                )
                .jobResourcePutHandler(
                        request -> JobResourcePutResponse.Builder.builder()
                                .status200(org.codingmatters.poomjobs.types.api.jobresourceputresponse.Status200.Builder.builder()
                                        .location("/poomjobs/jobs/" + request.jobId())
                                        .build())
                                .build()
                )
                .build();

        JsonFactory factory = new JsonFactory();

        this.server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    System.out.println(exchange.getRelativePath());

                    if(exchange.getRelativePath().matches("/poomjobs/jobs/?")) {
                        if(exchange.getRequestMethod().toString().toUpperCase().equals("POST")) {
                            JsonParser parser = factory.createParser(exchange.getInputStream());
                            JobCollectionPostResponse response = handlers.jobCollectionPostHandler().apply(
                                    JobCollectionPostRequest.Builder.builder()
                                            .payload(new JobReader().read(parser))
                                            .build()
                            );
                            if(response.status201() != null) {
                                exchange.setStatusCode(201);
                                exchange.getResponseHeaders().add(HttpString.tryFromString("Location"), response.status201().location());
                            } else if(response.status500() != null) {
                                exchange.setStatusCode(500);
                                try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                    try(JsonGenerator generator = factory.createGenerator(out)) {
                                        new ErrorWriter().write(generator, response.status500().paload());
                                    }
                                    exchange.getResponseSender().send(out.toString());
                                }
                            }
                        }
                    } else if(exchange.getRelativePath().matches("/poomjobs/jobs/[^/]+/?")) {
                        Matcher matcher = Pattern.compile("/poomjobs/jobs/([^/]+)/?").matcher(exchange.getRelativePath());
                        matcher.matches();
                        String jobIdUriParameter = matcher.group(1);
                        if(exchange.getRequestMethod().toString().toUpperCase().equals("GET")) {
                            JobResourceGetResponse response = handlers.jobResourceGetHandler().apply(JobResourceGetRequest.Builder.builder()
                                    .jobId(jobIdUriParameter)
                                    .build());
                            if(response.status200() != null) {
                                exchange.setStatusCode(200);
                                try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                    try(JsonGenerator generator = factory.createGenerator(out)) {
                                        new JobWriter().write(generator, response.status200().payload());
                                    }
                                    exchange.getResponseSender().send(out.toString());
                                }
                            } else if(response.status404() != null) {
                                exchange.setStatusCode(404);
                                try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                    try(JsonGenerator generator = factory.createGenerator(out)) {
                                        new ErrorWriter().write(generator, response.status500().payload());
                                    }
                                    exchange.getResponseSender().send(out.toString());
                                }
                            } else if(response.status500() != null) {
                                exchange.setStatusCode(500);
                                try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                    try(JsonGenerator generator = factory.createGenerator(out)) {
                                        new ErrorWriter().write(generator, response.status500().payload());
                                    }
                                    exchange.getResponseSender().send(out.toString());
                                }
                            }
                        } else if(exchange.getRequestMethod().toString().toUpperCase().equals("PUT")) {

                        }




                    }
                }).build();
        this.server.start();

        this.baseUrl = "http://localhost:" + port;
    }

    @After
    public void tearDown() throws Exception {
        this.server.stop();
    }

    @Test
    public void getJob() throws Exception {
        Response response = this.client.newCall(new Request.Builder()
                .url(this.baseUrl + "/poomjobs/jobs/12")
                .get()
                .build())
                .execute();

        assertThat(response.code(), is(200));
        assertThat(response.body().contentType().type(), is("application"));
        assertThat(response.body().contentType().subtype(), is("json"));

        JsonFactory factory = new JsonFactory();
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
}
