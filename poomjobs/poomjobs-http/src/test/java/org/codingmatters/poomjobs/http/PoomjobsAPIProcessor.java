package org.codingmatters.poomjobs.http;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.codingmatters.http.api.Processor;
import org.codingmatters.http.api.RequestDeleguate;
import org.codingmatters.http.api.ResponseDeleguate;
import org.codingmatters.poomjobs.types.api.*;
import org.codingmatters.poomjobs.types.types.json.ErrorWriter;
import org.codingmatters.poomjobs.types.types.json.JobReader;
import org.codingmatters.poomjobs.types.types.json.JobWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by nelt on 4/27/17.
 */
public class PoomjobsAPIProcessor implements Processor {
    private final String apiRelativePath;
    private final JsonFactory factory;
    private final PoomjobsAPIHandlers handlers;

    public PoomjobsAPIProcessor(String apiRelativePath, JsonFactory factory, PoomjobsAPIHandlers handlers) {
        this.apiRelativePath = apiRelativePath;
        this.factory = factory;
        this.handlers = handlers;
    }

    @Override
    public void process(RequestDeleguate requestDeleguate, ResponseDeleguate responseDeleguate) throws IOException {
        responseDeleguate.contenType("application/json; charset=utf-8");

        if(requestDeleguate.pathMatcher("/" + this.apiRelativePath + "/jobs/?").matches()) {
            if(requestDeleguate.method().equals(RequestDeleguate.Method.POST)) {
                JsonParser parser = this.factory.createParser(requestDeleguate.payload());
                JobCollectionPostResponse response = this.handlers.jobCollectionPostHandler().apply(
                        JobCollectionPostRequest.Builder.builder()
                                .payload(new JobReader().read(parser))
                                .build()
                );
                if (response.status201() != null) {
                    responseDeleguate
                            .status(201)
                            .addHeader(
                                    "Location",
                                    requestDeleguate.absolutePath(apiRelativePath + response.status201().location())
                            );
                } else if (response.status500() != null) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        try (JsonGenerator generator = factory.createGenerator(out)) {
                            new ErrorWriter().write(generator, response.status500().payload());
                        }
                        responseDeleguate.status(500).payload(out.toString(), "utf-8");
                    }
                }
            }
        } else if (requestDeleguate.pathMatcher("/" + apiRelativePath + "/jobs/[^/]+/?").matches()) {
            Map<String, String> pathParameters = requestDeleguate.pathParameters("/" + apiRelativePath + "/jobs/{jobId}/?");
            if(requestDeleguate.method().equals(RequestDeleguate.Method.GET)) {
                JobResourceGetResponse response = handlers.jobResourceGetHandler().apply(
                        JobResourceGetRequest.Builder.builder()
                                .jobId(pathParameters.get("jobId"))
                                .build()
                );
                if (response.status200() != null) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        try (JsonGenerator generator = factory.createGenerator(out)) {
                            new JobWriter().write(generator, response.status200().payload());
                        }
                        responseDeleguate.status(200).payload(out.toString(), "utf-8");
                    }
                } else if (response.status404() != null) {
                    if (response.status404().payload() != null) {
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            try (JsonGenerator generator = factory.createGenerator(out)) {
                                new ErrorWriter().write(generator, response.status404().payload());
                            }
                            responseDeleguate.status(404).payload(out.toString(), "utf-8");
                        }
                    }
                } else if (response.status500() != null) {
                    if (response.status500().payload() != null) {
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            try (JsonGenerator generator = factory.createGenerator(out)) {
                                new ErrorWriter().write(generator, response.status500().payload());
                            }
                            responseDeleguate.status(500).payload(out.toString(), "utf-8");
                        }
                    }
                }
            } else if (requestDeleguate.method().equals(RequestDeleguate.Method.PUT)) {
                JsonParser parser = factory.createParser(requestDeleguate.payload());
                JobResourcePutResponse response = handlers.jobResourcePutHandler().apply(
                        JobResourcePutRequest.Builder.builder()
                                .jobId(pathParameters.get("jobId"))
                                .payload(new JobReader().read(parser))
                                .build()
                );
                if (response.status200() != null) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        try (JsonGenerator generator = factory.createGenerator(out)) {
                            new JobWriter().write(generator, response.status200().payload());
                        }
                        responseDeleguate.status(200).payload(out.toString(), "utf-8");
                    }
                } else if (response.status404() != null) {
                    if (response.status404().payload() != null) {
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            try (JsonGenerator generator = factory.createGenerator(out)) {
                                new ErrorWriter().write(generator, response.status404().payload());
                            }
                            responseDeleguate.status(404).payload(out.toString(), "utf-8");
                        }
                    }
                } else if (response.status500() != null) {
                    if (response.status500().payload() != null) {
                        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            try (JsonGenerator generator = factory.createGenerator(out)) {
                                new ErrorWriter().write(generator, response.status500().payload());
                            }
                            responseDeleguate.status(500).payload(out.toString(), "utf-8");
                        }
                    }
                }
            }
        }
    }
}
