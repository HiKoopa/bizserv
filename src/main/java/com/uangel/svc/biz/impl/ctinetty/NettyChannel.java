package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CtiMessage;
import com.uangel.svc.biz.cti.NewCall;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

// netty connection
public class NettyChannel {

    private Channel channel;
    private final NettyChannelStatusListener client;

    public NettyChannel(NettyChannelStatusListener client) {
        this.client = client;
    }

    // 새로운 connection 을 만드는 함수
    public static CompletableFuture<NettyChannel> newConnection(EventLoopGroup workerGroup, NettyChannelStatusListener client, CtiMessageHandler handler, String addr , int port) {
        NettyChannel connection = new NettyChannel(client);
        CompletableFuture<NettyChannel> ret = new CompletableFuture<>();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);

        b.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                // connect 된 channel 의 pipeline 구성
                // inbound 는 위에서 아래로
                // outbound 는 아래서 위로 진행됨
                ch.pipeline().addLast(new CtiDecoder() );
                ch.pipeline().addLast(new CtiEncoder());
                ch.pipeline().addLast(new NettyChannelHandler(connection, handler));
            }
        });

        // connect 는 future 를 리턴함
        var cf = b.connect(addr, port);

        // future 에 addListener 를 사용하여
        // java future 로 변환
        cf.addListener((ChannelFuture future) -> {
            // lambda 에 ChannelFuture 타입을 지정하지 않으면  그냥 Future 타입이 되어버려
            // channel을 리턴받을 수 없으니 주의

            if (future.isSuccess()) {
                // 성공했을 때
                connection.channel = future.channel();
                ret.complete(connection);
            } else {
                // 실패했을 때
                System.out.println("connect failed");
                ret.completeExceptionally(future.cause());
            }
        });
        return ret;
    }

    public void connected(Channel channel) {
        client.connected(this);
    }

    public void disconnected(Channel channel) {
        client.disconnected(this);
    }

    public void close() {
        this.channel.close();
    }

    public CompletableFuture<Void> sendMessage(CtiMessage msg) {
        CompletableFuture<Void> promise = new CompletableFuture<>();

        channel.writeAndFlush(msg).addListener(future -> {
            if (future.isSuccess()) {
                promise.complete(null);
            } else {
                promise.completeExceptionally(future.cause());
            }
        });

        return promise;
    }
}
