package org.codingmatters.rest.server.netty;

import io.netty.handler.codec.http.HttpUtil;
import org.codingmatters.rest.api.Processor;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.codingmatters.rest.netty.utils.DynamicByteBuffer;
import org.codingmatters.rest.netty.utils.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProcessorRequestHandler extends HttpRequestHandler {
    static private final Logger log = LoggerFactory.getLogger(ProcessorRequestHandler.class);

    private final Processor processor;
    private final String host;
    private final int port;

    public ProcessorRequestHandler(Processor processor, String host, int port) {
        this.processor = processor;
        this.host = host;
        this.port = port;
    }

    @Override
    protected HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body) {
        NettyHttpRequestDeleguate requestDelegate;
        try {
            requestDelegate = new NettyHttpRequestDeleguate(this.host, this.port, request, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NettyHttpResponseDeleguate responseDeleguate = new NettyHttpResponseDeleguate(HttpUtil.isKeepAlive(request));
        try {
            this.processor.process(requestDelegate, responseDeleguate);
        } catch (IOException e) {
            log.error("exception thrown from processor (" + this.processor.getClass().getName() + ")", e);
            responseDeleguate
                    .status(500)
            ;
            return responseDeleguate.response();
        } catch(Throwable e) {
            log.error("[GRAVE] unexpected exception thrown from processor (" + this.processor.getClass().getName() + ")", e);
            responseDeleguate
                    .status(500)
            ;
            return responseDeleguate.response();
        }
        try {
            return responseDeleguate.response();
        } finally {
            try {
                responseDeleguate.close();
            } catch (Exception e) {
                log.error("[GRAVE] potential resource leak, failed closing NettyHttpResponseDeleguate", e);
            }
        }
    }
}
