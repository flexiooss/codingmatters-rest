package org.codingmatters.rest.netty.utils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class HttpServer {
    static private final Logger log = LoggerFactory.getLogger(HttpServer.class);

    static public HttpServer testServer(HandlerSupplier handlerSupplier) {
        try {
            ServerSocket freePortSocket = new ServerSocket(0);
            int port = freePortSocket.getLocalPort();
            freePortSocket.close();
            return new HttpServer(NettyHttpConfig.builder().host("localhost").port(port).build(), handlerSupplier);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public HttpServer server(NettyHttpConfig config, HandlerSupplier handlerSupplier) {
        return new HttpServer(config, handlerSupplier);
    }

    @FunctionalInterface
    public interface HandlerSupplier {
        HttpRequestHandler get(String host, int port);
    }

    static private NettyHttpConfig DEFAULT = NettyHttpConfig.builder()
            .host("0.0.0.0").port(8888)
            .bossCount(0).workerCount(0)
            .maxInitialLineLength(2 * HttpObjectDecoder.DEFAULT_MAX_INITIAL_LINE_LENGTH)
            .maxHeaderSize(2 * HttpObjectDecoder.DEFAULT_MAX_HEADER_SIZE)
            .maxChunkSize(HttpObjectDecoder.DEFAULT_MAX_CHUNK_SIZE)
            .initialBufferSize(HttpObjectDecoder.DEFAULT_INITIAL_BUFFER_SIZE)
            .maxPayloadSize(100 * 1024 * 1024)
            .validateHeaders(HttpObjectDecoder.DEFAULT_VALIDATE_HEADERS)
            .allowPartialChunks(HttpObjectDecoder.DEFAULT_ALLOW_PARTIAL_CHUNKS)
            .allowDuplicateContentLengths(HttpObjectDecoder.DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS)
            .build();

    private final HandlerSupplier handlerSupplier;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private ChannelFuture runningChannel;

    private final NettyHttpConfig config;

    private HttpServer(NettyHttpConfig config, HandlerSupplier handlerSupplier) {
        this.config = config == null ? DEFAULT :
                NettyHttpConfig.builder()
                        .host(config.opt().host().orElse(DEFAULT.host()))
                        .port(config.opt().port().orElse(DEFAULT.port()))
                        .bossCount(config.opt().bossCount().orElse(DEFAULT.bossCount()))
                        .workerCount(config.opt().workerCount().orElse(DEFAULT.workerCount()))
                        .maxInitialLineLength(config.opt().maxInitialLineLength().orElse(DEFAULT.maxInitialLineLength()))
                        .maxHeaderSize(config.opt().maxHeaderSize().orElse(DEFAULT.maxHeaderSize()))
                        .maxChunkSize(config.opt().maxChunkSize().orElse(DEFAULT.maxChunkSize()))
                        .initialBufferSize(config.opt().initialBufferSize().orElse(DEFAULT.initialBufferSize()))
                        .maxPayloadSize(config.opt().maxPayloadSize().orElse(DEFAULT.maxPayloadSize()))
                        .validateHeaders(config.opt().validateHeaders().orElse(DEFAULT.validateHeaders()))
                        .allowPartialChunks(config.opt().allowPartialChunks().orElse(DEFAULT.allowPartialChunks()))
                        .allowDuplicateContentLengths(config.opt().allowDuplicateContentLengths().orElse(DEFAULT.allowDuplicateContentLengths()))
                        .host(config.opt().host().orElse(DEFAULT.host()))
                        .build();
        this.handlerSupplier = handlerSupplier;
        this.bossGroup = new NioEventLoopGroup(this.config.bossCount());
        this.workerGroup = new NioEventLoopGroup(this.config.workerCount());

    }

    public String host() {
        return this.config.host();
    }
    public int port() {
        return config.port();
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpServerCodec(
                                        config.maxInitialLineLength(),
                                        config.maxHeaderSize(),
                                        config.maxChunkSize(),
                                        config.validateHeaders(),
                                        config.initialBufferSize(),
                                        config.allowDuplicateContentLengths(),
                                        config.allowPartialChunks()
                                ))
                                .addLast(new HttpObjectAggregator(config.maxPayloadSize()))
                                .addLast(new ChunkedWriteHandler())
                                .addLast(handlerSupplier.get(config.host(), config.port()))
                        ;
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                ;

        log.info("starting...");
        this.runningChannel = b.bind(config.port()).sync();
        log.info("started.");
    }

    public void shutdown() {
        log.info("shutting down...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("shutdown.");
    }

    public void awaitTermination() throws InterruptedException {
        log.info("waiting for close...");
        runningChannel.channel().closeFuture().sync();
        log.info("closed.");
    }
}
