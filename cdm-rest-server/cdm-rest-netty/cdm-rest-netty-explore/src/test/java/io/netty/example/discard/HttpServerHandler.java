package io.netty.example.discard;

import io.netty.handler.codec.http.*;
import org.codingmatters.rest.netty.utils.DynamicByteBuffer;
import org.codingmatters.rest.netty.utils.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class HttpServerHandler extends HttpRequestHandler {
    static private final Logger log = LoggerFactory.getLogger(HttpServerHandler.class);

    protected HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        long size = body != null ? body.size() : 0L;
        String content = "Server says, hello ! Body size is " + size + ".";

        response.content().writeBytes(content.getBytes(StandardCharsets.UTF_8));
        if(HttpUtil.isKeepAlive(request)) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        }

        this.logBody(request, body);

        return response;
    }

    private static void logBody(HttpRequest request, DynamicByteBuffer body) {
        log.info("--------------------------------");
        log.info("request body was {} bytes of {} : --------", body.size(), request.headers().get(HttpHeaderNames.CONTENT_TYPE, null));
        if (request.headers().get(HttpHeaderNames.CONTENT_TYPE, "nottext").startsWith("text/")) {

            char[] buffer = new char[1024];
            try (Reader reader = new InputStreamReader(body.stream())) {
                for (int read = reader.read(buffer); read != -1; read = reader.read(buffer)) {
                    log.info(new String(buffer, 0, read));
                }
            } catch (IOException e) {
                log.error("failed reading request body", e);
            }
        }
        log.info("--------------------------------");
    }
}
