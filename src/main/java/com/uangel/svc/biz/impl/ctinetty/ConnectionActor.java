package com.uangel.svc.biz.impl.ctinetty;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.uangel.svc.biz.actorutil.ResponseType;
import com.uangel.svc.biz.impl.ctimessage.CtiMessage;
import com.uangel.svc.biz.impl.ctimessage.LoginReq;
import com.uangel.svc.biz.impl.ctimessage.LoginResp;
import com.uangel.svc.biz.impl.ctimessage.NewCall;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionActor extends AbstractActorWithStash implements NettyChannelStatusListener, CtiMessageHandler {

    private CtiRequires ctiRequires;
    private EventLoopGroup loopGroup;
    private String ipAddress;
    private int port;
    private String clientName;

    private NettyChannel channel;

    public ConnectionActor(CtiRequires ctiRequires, EventLoopGroup loopGroup, String ipAddress, int port, String clientName) {
        this.ctiRequires = ctiRequires;
        this.loopGroup = loopGroup;
        this.ipAddress = ipAddress;
        this.port = port;
        this.clientName = clientName;
    }

    @SuppressWarnings("CodeBlock2Expr")
    public static Props props(CtiRequires ctiRequires, EventLoopGroup loopGroup, String ipAddress, int port, String clientName) {
        return Props.create(ConnectionActor.class, () -> {
            return new ConnectionActor(ctiRequires, loopGroup, ipAddress, port, clientName);
        });
    }

    @Override
    public void handleMessage(CtiMessage msg) {
        self().tell(msg, ActorRef.noSender());
    }

    class initialState {
        public initialState() {
            log.info("actor {} become initialState", self());
        }

        @SuppressWarnings("CodeBlock2Expr")
        public Receive createReceive() {
            return receiveBuilder()
                    .match(NettyChannel.class, this::onConnected)
                    .match(Throwable.class, this::onFailed)
                    .match(messageNewCall.class, messageNewCall -> {
                        stash();
                    })
                    .matchAny(r -> System.out.println("unexpected message : " + r))
                    .build();
        }

        private void onConnected(NettyChannel nettyChannel) {
            //Send Login Request
            channel = nettyChannel;
            nettyChannel.sendMessage(new LoginReq("callID123", clientName));
            getContext().become(new WaitingLoginResp().createReceive());
        }

        private void onFailed(Throwable throwable) {
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(r -> log.info("unexpected message : {} " , r))
                .build();
    }

    @Override
    public void connected(NettyChannel nettyChannel) {
        self().tell(nettyChannel, ActorRef.noSender());
    }

    @Override
    public void disconnected(NettyChannel nettyChannel) {
        self().tell(new messageDisconnected(nettyChannel), ActorRef.noSender());
    }

    static class messageNewCall implements ResponseType<Void> {
        private String CallID;
        private String CalledNum;

        public messageNewCall(String callID, String calledNum) {
            CallID = callID;
            CalledNum = calledNum;
        }

        public String getCallID() {
            return CallID;
        }

        public String getCalledNum() {
            return CalledNum;
        }
    }

    @Override
    public void preStart() {
        getContext().become(new initialState().createReceive());
        var f = NettyChannel.newConnection(loopGroup, this, this, ipAddress, port);
        f.whenComplete((nettyChannel, throwable) -> {
            if (throwable != null) {
                self().tell(throwable, ActorRef.noSender());
            }
        });
    }

    private class WaitingLoginResp {
        //TODO 생성자에서 타이머 Start하고, 만료되면 다시로그인? 새로접속?


        public WaitingLoginResp() {
            log.info("actor {} become WaitingLoginResp", self());

        }

        public Receive createReceive() {
            return receiveBuilder()
                    .match(LoginResp.class, this::onLoginResp)
                    .match(messageDisconnected.class, this::onMessageDisconnected)
                    .matchAny(r -> stash())
                    .build();
        }

        //TODO Interval 필요
        private void onMessageDisconnected(messageDisconnected req) {
            preStart();
        }

        private void onLoginResp(LoginResp loginResp) {
            unstashAll();
            getContext().become(new NormalState().createReceive());
        }
    }

    private class NormalState {

        public NormalState() {
            unstashAll();
            log.info("actor {} become NormalState", self());

        }

        public Receive createReceive() {
            return receiveBuilder()
                    .match(messageNewCall.class, this::onMessageNewCall)
                    .match(messageDisconnected.class, this::onMessageDisconnected)
                    .matchAny(r -> log.info("AT Normal State unexpected message : {} " , r))
                    .build();
        }

        private void onMessageDisconnected(messageDisconnected req) {
            preStart();
        }

        private void onMessageNewCall(messageNewCall req) {
            var f = channel.sendMessage(new NewCall(req.CallID, req.CalledNum));
            req.sendFutureResponse(sender(), f, ActorRef.noSender());
        }
    }

    private static class messageDisconnected {
        public messageDisconnected(NettyChannel nettyChannel) {
        }
    }
}
