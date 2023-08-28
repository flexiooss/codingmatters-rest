package io.netty.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer {

    static private final Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new TcpServer(port).run();
    }

    private int port;

    public TcpServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap() // (2)
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {
                             log.info("initChannel");
                             ch.pipeline().addLast(new TcpServerHandler());
                         }
                     })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // (6)
                    ;
            // Bind and start to accept incoming connections.
            log.info("starting...");
            ChannelFuture f = b.bind(port).sync(); // (7)
            log.info("started.");

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            log.info("waiting for close...");
            f.channel().closeFuture().sync();
            log.info("closed.");
        } finally {
            log.info("finalizing...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("finalized.");
        }
    }
}
