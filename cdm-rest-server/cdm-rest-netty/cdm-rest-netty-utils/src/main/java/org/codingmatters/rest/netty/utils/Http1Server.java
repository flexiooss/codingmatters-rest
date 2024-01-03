package org.codingmatters.rest.netty.utils;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.codingmatters.rest.netty.utils.config.NettyHttpConfig;

import java.io.IOException;
import java.net.ServerSocket;

public class Http1Server extends AbstratHttpServer {

    static public Http1Server testServer(HandlerSupplier handlerSupplier) {
        try {
            ServerSocket freePortSocket = new ServerSocket(0);
            int port = freePortSocket.getLocalPort();
            freePortSocket.close();
            return new Http1Server(NettyHttpConfig.builder().host("localhost").port(port).build(), handlerSupplier);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public Http1Server server(NettyHttpConfig config, HandlerSupplier handlerSupplier) {
        return new Http1Server(config, handlerSupplier);
    }
    public Http1Server(NettyHttpConfig config, HandlerSupplier handlerSupplier) {
        super(config, handlerSupplier);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        HttpServerCodec sourceCodec = new HttpServerCodec(
                this.config().maxInitialLineLength(),
                this.config().maxHeaderSize(),
                this.config().maxChunkSize(),
                this.config().validateHeaders(),
                this.config().initialBufferSize(),
                this.config().allowDuplicateContentLengths(),
                this.config().allowPartialChunks()
        );
        ch.pipeline()
                .addLast(sourceCodec)
                .addLast(new HttpObjectAggregator(this.config().maxPayloadSize()))
                .addLast(this.handlerSupplier().get(this.config().host(), this.config().port()))
        ;
    }
}
