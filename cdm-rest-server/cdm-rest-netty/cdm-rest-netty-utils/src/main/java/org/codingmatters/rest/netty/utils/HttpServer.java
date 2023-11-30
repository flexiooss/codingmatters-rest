package org.codingmatters.rest.netty.utils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
            return new HttpServer("localhost", port, handlerSupplier, new NioEventLoopGroup(), new NioEventLoopGroup());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public HttpServer server(String host, int port, HandlerSupplier handlerSupplier, int bossCount, int workerCount) {
        return new HttpServer(host, port, handlerSupplier, new NioEventLoopGroup(bossCount), new NioEventLoopGroup(workerCount));
    }

    @FunctionalInterface
    public interface HandlerSupplier {
        HttpRequestHandler get(String host, int port);
    }

    private final String host;
    private final int port;
    private final HandlerSupplier handlerSupplier;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private ChannelFuture runningChannel;

    private HttpServer(String host, int port, HandlerSupplier handlerSupplier, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.host = host;
        this.port = port;
        this.handlerSupplier = handlerSupplier;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    public String host() {
        return this.host;
    }
    public int port() {
        return port;
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap() // (2)
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // (3)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        log.trace("initChannel");
                        ch.pipeline()
                                .addLast(new HttpServerCodec(
                                        2 * HttpObjectDecoder.DEFAULT_MAX_INITIAL_LINE_LENGTH,
                                        2 * HttpObjectDecoder.DEFAULT_MAX_HEADER_SIZE,
                                        HttpObjectDecoder.DEFAULT_MAX_CHUNK_SIZE
                                ))
                                .addLast(handlerSupplier.get(host, port))
                        ;
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
                ;
        // Bind and start to accept incoming connections.
        log.info("starting...");
        this.runningChannel = b.bind(port).sync(); // (7)
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
