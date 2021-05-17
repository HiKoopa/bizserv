package com.uangel.svc.biz.impl.ctinetty;

public interface NettyChannelStatusListener {
    void connected(NettyChannel nettyChannel);

    void disconnected(NettyChannel nettyChannel);
}
