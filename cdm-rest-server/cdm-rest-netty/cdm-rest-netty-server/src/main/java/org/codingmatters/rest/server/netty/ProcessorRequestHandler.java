package org.codingmatters.rest.server.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.codingmatters.rest.api.Processor;
import org.codingmatters.rest.netty.utils.DynamicByteBuffer;
import org.codingmatters.rest.netty.utils.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessorRequestHandler extends HttpRequestHandler implements Closeable {
    static private final Logger log = LoggerFactory.getLogger(ProcessorRequestHandler.class);

    private final Processor processor;
    private final String host;
    private final int port;
    private final ExecutorService sseExecutor;

    public ProcessorRequestHandler(Processor processor, String host, int port) {
        this(processor, host, port, Executors.newCachedThreadPool());
    }

    public ProcessorRequestHandler(Processor processor, String host, int port, ExecutorService sseExecutor) {
        this.processor = processor;
        this.host = host;
        this.port = port;
        this.sseExecutor = sseExecutor;
    }

    @Override
    public void close() {
        this.sseExecutor.shutdown();
    }

    @Override
    protected HttpResponse processResponse(ChannelHandlerContext ctx, HttpRequest request, DynamicByteBuffer body) {
        String accept = request.headers().get(HttpHeaderNames.ACCEPT);
        boolean isSse = accept != null && accept.contains("text/event-stream");

        NettyHttpRequestDeleguate requestDelegate;
        try {
            requestDelegate = new NettyHttpRequestDeleguate(this.host, this.port, request, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (isSse) {
            NettyHttpResponseDeleguate sseDelegate = new NettyHttpResponseDeleguate(true, ctx);
            this.sseExecutor.submit(() -> {
                try {
                    this.processor.process(requestDelegate, sseDelegate);
                } catch (Exception e) {
                    log.error("exception in SSE processor (" + this.processor.getClass().getName() + ")", e);
                } finally {
                    ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
                            .addListener(ChannelFutureListener.CLOSE);
                }
            });
            return null; // Handled asynchronously
        }

        NettyHttpResponseDeleguate responseDeleguate = new NettyHttpResponseDeleguate(HttpUtil.isKeepAlive(request));
        try {
            this.processor.process(requestDelegate, responseDeleguate);
        } catch (IOException e) {
            log.error("exception thrown from processor (" + this.processor.getClass().getName() + ")", e);
            responseDeleguate.status(500);
            return responseDeleguate.response();
        } catch (Throwable e) {
            log.error("[GRAVE] unexpected exception thrown from processor (" + this.processor.getClass().getName() + ")", e);
            responseDeleguate.status(500);
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
