package org.codingmatters.poomjobs.http;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.codingmatters.poomjobs.types.api.*;
import org.codingmatters.poomjobs.types.types.json.ErrorWriter;
import org.codingmatters.poomjobs.types.types.json.JobReader;
import org.codingmatters.poomjobs.types.types.json.JobWriter;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.api.RequestDelegate;
import org.codingmatters.rest.api.ResponseDelegate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by nelt on 4/27/17.
 */
public class PoomjobsAPIProcessor implements Processor {
    private final String apiPath;
    private final JsonFactory factory;
    private final PoomjobsAPIHandlers handlers;

    public PoomjobsAPIProcessor(String apiPath, JsonFactory factory, PoomjobsAPIHandlers handlers) {
        this.apiPath = apiPath;
        this.factory = factory;
        this.handlers = handlers;
    }

    @Override
    public void process(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        responseDelegate.contenType("application/json; charset=utf-8");
        if(requestDelegate.pathMatcher(this.apiPath + "/jobs/?").matches()) {
            if(requestDelegate.method().equals(RequestDelegate.Method.POST)) {
                this.processJobCollectionPostRequest(requestDelegate, responseDelegate);
            }
        } else if (requestDelegate.pathMatcher(this.apiPath + "/jobs/[^/]+/?").matches()) {
            if(requestDelegate.method().equals(RequestDelegate.Method.GET)) {
                this.processJobResourceGetRequest(requestDelegate, responseDelegate);
            } else if (requestDelegate.method().equals(RequestDelegate.Method.PUT)) {
                this.processJobResourcePutRequest(requestDelegate, responseDelegate);
            }
        }
    }

    private void processJobCollectionPostRequest(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        JsonParser parser = this.factory.createParser(requestDelegate.payload());
        JobCollectionPostResponse response = this.handlers.jobCollectionPostHandler().apply(
                JobCollectionPostRequest.Builder.builder()
                        .payload(new JobReader().read(parser))
                        .build()
        );
        if (response.status201() != null) {
            responseDelegate
                    .status(201)
                    .addHeader(
                            "Location",
                            requestDelegate.absolutePath(apiPath + response.status201().location())
                    );
        } else if (response.status500() != null) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (JsonGenerator generator = factory.createGenerator(out)) {
                    new ErrorWriter().write(generator, response.status500().payload());
                }
                responseDelegate.status(500).payload(out.toString(), "utf-8");
            }
        }
    }

    private void processJobResourceGetRequest(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        Map<String, List<String>> uriParameters = requestDelegate.uriParameters(this.apiPath + "/jobs/{jobId}/?");
        JobResourceGetResponse response = handlers.jobResourceGetHandler().apply(
                JobResourceGetRequest.Builder.builder()
                        .jobId(uriParameters.get("jobId") != null && ! uriParameters.get("jobId").isEmpty() ? uriParameters.get("jobId").get(0) : null)
                        .build()
        );
        if (response.status200() != null) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (JsonGenerator generator = factory.createGenerator(out)) {
                    new JobWriter().write(generator, response.status200().payload());
                }
                responseDelegate.status(200).payload(out.toString(), "utf-8");
            }
        } else if (response.status404() != null) {
            if (response.status404().payload() != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    try (JsonGenerator generator = factory.createGenerator(out)) {
                        new ErrorWriter().write(generator, response.status404().payload());
                    }
                    responseDelegate.status(404).payload(out.toString(), "utf-8");
                }
            }
        } else if (response.status500() != null) {
            if (response.status500().payload() != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    try (JsonGenerator generator = factory.createGenerator(out)) {
                        new ErrorWriter().write(generator, response.status500().payload());
                    }
                    responseDelegate.status(500).payload(out.toString(), "utf-8");
                }
            }
        }
    }

    private void processJobResourcePutRequest(RequestDelegate requestDelegate, ResponseDelegate responseDelegate) throws IOException {
        Map<String, List<String>> uriParameters = requestDelegate.uriParameters(this.apiPath + "/jobs/{jobId}/?");
        JsonParser parser = factory.createParser(requestDelegate.payload());
        JobResourcePutResponse response = handlers.jobResourcePutHandler().apply(
                JobResourcePutRequest.Builder.builder()
                        .jobId(uriParameters.get("jobId") != null && ! uriParameters.get("jobId").isEmpty() ? uriParameters.get("jobId").get(0) : null)
                        .payload(new JobReader().read(parser))
                        .build()
        );
        if (response.status200() != null) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                try (JsonGenerator generator = factory.createGenerator(out)) {
                    new JobWriter().write(generator, response.status200().payload());
                }
                responseDelegate.status(200).payload(out.toString(), "utf-8");
            }
        } else if (response.status404() != null) {
            if (response.status404().payload() != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    try (JsonGenerator generator = factory.createGenerator(out)) {
                        new ErrorWriter().write(generator, response.status404().payload());
                    }
                    responseDelegate.status(404).payload(out.toString(), "utf-8");
                }
            }
        } else if (response.status500() != null) {
            if (response.status500().payload() != null) {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    try (JsonGenerator generator = factory.createGenerator(out)) {
                        new ErrorWriter().write(generator, response.status500().payload());
                    }
                    responseDelegate.status(500).payload(out.toString(), "utf-8");
                }
            }
        }
    }
}
