package com.uangel.svc.biz.impl.ctisim;

import com.uangel.svc.biz.cti.CtiMessage;
import com.uangel.svc.biz.cti.LoginReq;
import com.uangel.svc.biz.cti.NewCall;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class ServerHandler extends ChannelDuplexHandler {
    private CtiServer server;
    private ServiceLogic logic;

    public ServerHandler(CtiServer server, ServiceLogic logic) {
        this.server = server;
        this.logic = logic;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        System.out.println("connected");
        server.channelConnected(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("disconnected");
        server.channelDisconnected(ctx.channel());

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof CtiMessage) {
            var request = (CtiMessage)msg;
            var mtype = ((CtiMessage) msg).messageType();
            if (mtype.equals("LoginReq")) {
                logic.onLoginReq(ctx.channel(), (LoginReq) request);
            } else if (mtype.equals("NewCall")) {
                logic.onNewCall(ctx.channel(), (NewCall) request);
            }
            //consumer.accept(new Request(server , ctx.channel(),  request));
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise);

        //super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exception caught", cause);
    }
}
