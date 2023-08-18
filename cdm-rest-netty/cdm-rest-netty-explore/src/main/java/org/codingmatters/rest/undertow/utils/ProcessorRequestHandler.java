package org.codingmatters.rest.undertow.utils;

import org.codingmatters.rest.api.Processor;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProcessorRequestHandler extends HttpRequestHandler {
    static private final Logger log = LoggerFactory.getLogger(ProcessorRequestHandler.class);

    private final Processor processor;

    public ProcessorRequestHandler(Processor processor) {
        this.processor = processor;
    }

    @Override
    protected HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body) {
        NettyHttpRequestDeleguate requestDelegate = new NettyHttpRequestDeleguate(request, body);
        NettyHttpResponseDeleguate responseDeleguate = new NettyHttpResponseDeleguate();

        try {
            this.processor.process(requestDelegate, responseDeleguate);
        } catch (IOException e) {
            log.error("exception thrown from processor (" + this.processor.getClass().getName() + ")", e);
        } catch(Throwable e) {
            log.error("[GRAVE] unexpected exception thrown from processor (" + this.processor.getClass().getName() + ")", e);
        }
        return responseDeleguate.response();
    }
}
