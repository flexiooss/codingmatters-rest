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

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public abstract class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {
    static private final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private HttpRequest request;
    private boolean completelyRead = false;
    private DynamicByteBuffer body;


    protected abstract HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("finished handling channel");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            log.debug("here's the http request");
            this.request = (HttpRequest) msg;
        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            if (this.body == null) {
                log.debug("here's the first slice : {}", msg);
                this.body = new DynamicByteBuffer(1000);
            } else {
                log.debug("here's another slice : {}", msg);
            }
            this.body.accumulate(((HttpContent) msg).content());
            if (content instanceof LastHttpContent) {
                log.debug("content was last slice");
                this.completelyRead = true;
            }
        } else {
            log.debug("unexpected message type, ignoring : {} - {}", msg != null ? msg.getClass() : "null", msg);
        }

        if (this.completelyRead) {
            log.debug("done reading the http request completely, responding");

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

            this.cleanup();
        }
    }

    private HttpResponse buildResponse() {
        try {
            return this.processResponse(this.request, this.body);
        } catch (Throwable t) {
            log.error("[GRAVE] exception thrown by business code, should be catched.");
            FullHttpResponse errorResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
            errorResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            errorResponse.content().writeBytes("Unexpected error handling request.".getBytes(StandardCharsets.UTF_8));
            return errorResponse;
        }
    }

    private void cleanup() {
        this.request = null;
        this.completelyRead = false;
        if (this.body != null) {
            this.body.release();
            this.body = null;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error cauth while handling http request", cause);
        ctx.close();
    }
}