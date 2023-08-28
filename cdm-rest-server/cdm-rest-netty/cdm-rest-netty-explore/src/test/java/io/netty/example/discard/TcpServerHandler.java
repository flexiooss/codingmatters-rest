package io.netty.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    static Logger log = LoggerFactory.getLogger(TcpServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String content = ((ByteBuf) msg).toString(StandardCharsets.UTF_8).trim();
            log.info("message received : {}", content);

            String responseContent = "did you say \"" + content + "\" ?";

            ByteBuf response = ctx.alloc().buffer(responseContent.getBytes().length);
            response.writeBytes(responseContent.getBytes(StandardCharsets.UTF_8));
            ChannelFuture f = ctx.write(response);
            if("bye.".equals(content)) {
                log.info("client said bye, closing connection.");
                f.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        ctx.close();
                    }
                });
            }
            ctx.flush();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error handling request", cause);
    }
}
