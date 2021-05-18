package com.uangel.svc.biz.impl.ctisim;

import com.uangel.svc.biz.impl.ctinetty.CtiDecoder;
import com.uangel.svc.biz.impl.ctinetty.CtiEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CtiServerFactory implements AutoCloseable {

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();


    public CompletableFuture<CtiServer> newServer(int port, ServiceLogic logic) {

        var server = new CtiServer();
        CompletableFuture<CtiServer> ret = new CompletableFuture<>();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.channel(NioServerSocketChannel.class);

        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {

                socketChannel.pipeline().addLast(new CtiDecoder() );
                socketChannel.pipeline().addLast(new CtiEncoder());
                socketChannel.pipeline().addLast(new ServerHandler(server, logic));

            }
        });

        var cf = b.bind(port);
        cf.addListener(( ChannelFuture future) -> {

            if (future.isSuccess()) {
                System.out.println("bind success");
                server.Bind(future.channel());
                ret.complete(server);
            } else {
                System.out.println("bind failed");
                future.cause().printStackTrace();
                ret.completeExceptionally(future.cause());
            }
        });


        return ret;
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
