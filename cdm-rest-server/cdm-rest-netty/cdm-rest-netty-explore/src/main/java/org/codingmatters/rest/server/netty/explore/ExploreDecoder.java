package org.codingmatters.rest.server.netty.explore;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ExploreDecoder extends ByteToMessageDecoder {
    static private final Logger log = LoggerFactory.getLogger(ExploreDecoder.class);
    public static final String MSG_MARKER = ".\r\n";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String s = in.toString(StandardCharsets.UTF_8);
        if(s.endsWith(MSG_MARKER)) {
            in.readBytes(in.readableBytes());
            String[] splitted = s.substring(0, s.length() - MSG_MARKER.length()).trim().split("\n");
            log.info("good job");
            for (String msg : splitted) {
                out.add(msg);
                log.info("a message : " + msg);
            }
            log.info("done.");
        } else {
            log.debug("need more");
        }
    }
}
