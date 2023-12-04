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

public abstract class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    static private final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    protected abstract HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        log.debug("starting handling request...");
        if(request.decoderResult().isFailure()) {
            this.decoderError(ctx, request);
        } else {
            this.nominalResponse(ctx, request);
        }
        ctx.flush();
        log.debug("finished handling request");
    }



    private void decoderError(ChannelHandlerContext ctx, FullHttpRequest request) {
        if(request.decoderResult().cause() instanceof TooLongHttpHeaderException) {
            ctx.write(this.response(HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, "Request Header Fields Too Large"));
        } else if(request.decoderResult().cause() instanceof TooLongHttpLineException) {
            ctx.write(this.response(HttpResponseStatus.REQUEST_URI_TOO_LONG, "URI Too Long"));
        } else {
            log.error("[GRAVE] exception thrown while decoding request.", request.decoderResult().cause());
            ctx.write(this.errorResponse());
        }
    }

    private void nominalResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.trace("done reading the http request completely, responding");

        HttpResponse response = this.buildResponse(ctx, request);

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

    private HttpResponse buildResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
        DynamicByteBuffer body = new DynamicByteBuffer(30 * 1024);
        try {
            log.trace("building response");
            body.accumulate(request.content());
            return this.processResponse(request, body);
        } catch (Throwable t) {
            log.error("[GRAVE] exception thrown by business code, should be caught.", t);
            return this.errorResponse();
        } finally {
            body.release();
        }
    }

    private FullHttpResponse errorResponse() {
        return this.response(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Unexpected error handling request");
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
}