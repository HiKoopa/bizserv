package com.uangel.svc.biz.impl.ctinetty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class NettyChannelHandler extends ChannelDuplexHandler {
    public NettyChannelHandler(NettyChannel connection) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    }
}
