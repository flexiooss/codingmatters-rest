package org.codingmatters.rest.netty.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public abstract class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {
    static private final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private HttpRequest request;
//    private boolean completelyRead = false;
    private DynamicByteBuffer body;

    protected abstract HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.trace("finished handling channel");
        if(this.request == null) {
            log.error("[GRAVE] no request could be read.");
            ctx.write(this.errorResponse());
        } else if(this.request.decoderResult().isFailure()) {
            this.decoderError(ctx);
//        } else if (this.completelyRead) {
//        } else if (this.request.decoderResult().isFinished()) {
//            this.nominalResponse(ctx);
        } else {
            this.nominalResponse(ctx);
        }
        ctx.flush();
        this.cleanup();
        log.trace("finished handling channel : ctx flushed");
    }

    private void nominalResponse(ChannelHandlerContext ctx) {
        log.trace("done reading the http request completely, responding");

        HttpResponse response = this.buildResponse();

        if(HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieString != null) {
            Set<io.netty.handler.codec.http.cookie.Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                for (io.netty.handler.codec.http.cookie.Cookie cookie: cookies) {
                    response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                }
            }
        }

        ctx.write(response);
        if (!HttpUtil.isKeepAlive(request)) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void decoderError(ChannelHandlerContext ctx) {
        if(this.request.decoderResult().cause() instanceof TooLongHttpHeaderException) {
            ctx.write(this.response(HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "Request Header Fields Too Large"));
        } else if(this.request.decoderResult().cause() instanceof TooLongHttpLineException) {
            ctx.write(this.response(HttpResponseStatus.REQUEST_URI_TOO_LONG, "URI Too Long"));
        } else {
            log.error("[GRAVE] exception thrown while decoding request.", this.request.decoderResult().cause());
            ctx.write(this.errorResponse());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            log.trace("here's the http request : {}", msg.getClass().getName());
            this.request = (HttpRequest) msg;
        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            if (this.body == null) {
                log.trace("here's the first slice : {}", msg);
                this.body = new DynamicByteBuffer(1000);
            } else {
                log.trace("here's another slice : {}", msg);
            }
            this.body.accumulate(((HttpContent) msg).content());
//            if (content instanceof LastHttpContent) {
//                log.trace("content was last slice");
//                this.completelyRead = true;
//            }
        } else {
            log.error("[GRAVE] unexpected message type, ignoring : {} - {}", msg != null ? msg.getClass() : "null", msg);
        }
    }

    private HttpResponse buildResponse() {
        try {
            log.trace("building response");
            return this.processResponse(this.request, this.body);
        } catch (Throwable t) {
            log.error("[GRAVE] exception thrown by business code, should be caught.", t);
            return this.errorResponse();
        }
    }

    private FullHttpResponse response(HttpResponseStatus status, String message) {
        FullHttpResponse errorResponse = new DefaultFullHttpResponse(HTTP_1_1, status);
        errorResponse.setStatus(status);
        errorResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        errorResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        errorResponse.content().writeBytes(bytes);
        return errorResponse;
    }

    private FullHttpResponse errorResponse() {
        return this.response(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Unexpected error handling request");
    }

    private void cleanup() {
        this.request = null;
//        this.completelyRead = false;
        if (this.body != null) {
            this.body.release();
            this.body = null;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error caught while handling http request", cause);
        ctx.close();
    }
}