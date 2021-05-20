package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.impl.ctimessage.CtiMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyChannelHandler extends ChannelDuplexHandler {
    private NettyChannel connection;
    private CtiMessageHandler handler;

    public NettyChannelHandler(NettyChannel connection, CtiMessageHandler handler) {
        this.connection = connection;
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof CtiMessage) {
            handler.handleMessage((CtiMessage) msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        connection.connected(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        connection.disconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Modify later", cause);
    }
}
