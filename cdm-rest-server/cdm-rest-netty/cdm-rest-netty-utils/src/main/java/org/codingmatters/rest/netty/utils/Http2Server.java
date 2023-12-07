package org.codingmatters.rest.netty.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http2Server extends AbstratHttpServer {
    static private final Logger log = LoggerFactory.getLogger(Http2Server.class);

    public Http2Server(NettyHttpConfig config, HandlerSupplier handlerSupplier) {
        super(config, handlerSupplier);
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        final HttpServerCodec sourceCodec = new HttpServerCodec(
                this.config().maxInitialLineLength(),
                this.config().maxHeaderSize(),
                this.config().maxChunkSize(),
                this.config().validateHeaders(),
                this.config().initialBufferSize(),
                this.config().allowDuplicateContentLengths(),
                this.config().allowPartialChunks()
        );

        Http2Settings h2Settings = new Http2Settings()
                .maxHeaderListSize(2 * this.config().maxHeaderSize())
                .maxFrameSize(Http2CodecUtil.MAX_FRAME_SIZE_UPPER_BOUND)
                ;

        final HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec, protocol -> {
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
                return new Http2ServerUpgradeCodec(new Http2ConnectionHandlerBuilder()
                        .initialSettings(h2Settings)
                        .build());
            } else {
                return null;
            }
        });

        DefaultHttp2Connection connection = new DefaultHttp2Connection(true);
        InboundHttp2ToHttpAdapter listener = new InboundHttp2ToHttpAdapterBuilder(connection)
                .propagateSettings(true)
                .validateHttpHeaders(false)
                .maxContentLength(this.config().maxPayloadSize())
                .build();

        HttpToHttp2ConnectionHandler http2ToHttp1Converter = new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(listener)
                .initialSettings(h2Settings)
                .connection(connection).build();

        final CleartextHttp2ServerUpgradeHandler cleartextHttp2ServerUpgradeHandler =
                new CleartextHttp2ServerUpgradeHandler(
                        sourceCodec,
                        upgradeHandler,
                        http2ToHttp1Converter
                );

        p.addLast(cleartextHttp2ServerUpgradeHandler);
        p.addLast(this.http2Handler());
        p.addLast(this.http1Handler());
    }

    private SimpleChannelInboundHandler<HttpMessage> http1Handler() {
        return new SimpleChannelInboundHandler<HttpMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                log.debug("client doesn't support h2c, falling back to : " + msg.protocolVersion() + " (no upgrade was attempted)");
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addAfter(ctx.name(), null, handlerSupplier().get(config().host(), config().port()));
                pipeline.replace(this, null, new HttpObjectAggregator(config().maxPayloadSize()));
                ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            }
        };
    }

    private HttpRequestHandler http2Handler() {
        return new HttpRequestHandler() {
            @Override
            protected HttpResponse processResponse(HttpRequest request, DynamicByteBuffer body) {
                HttpResponse response = handlerSupplier().get(config().host(), config().port()).processResponse(request, body);
                response.headers().set(
                        HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(),
                        request.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text())
                );
                return response;
            }
        };
    }
}
