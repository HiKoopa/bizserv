package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CtiMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class NettyChannelHandler extends ChannelDuplexHandler {
    private CtiMessageHandler handler;

    public NettyChannelHandler(NettyChannel connection, CtiMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CtiMessage) {
            handler.handleMessage((CtiMessage) msg);
        }
    }
}
