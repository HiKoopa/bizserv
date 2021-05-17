package com.uangel.svc.biz.impl.ctinetty;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.uangel.svc.biz.actorutil.ResponseType;
import io.netty.channel.EventLoopGroup;

public class ConnectionActor extends AbstractActorWithStash implements NettyChannelStatusListener {

    private CtiRequires ctiRequires;
    private EventLoopGroup loopGroup;
    private String ipAddress;
    private int port;
    private String clientName;

    public ConnectionActor(CtiRequires ctiRequires, EventLoopGroup loopGroup, String ipAddress, int port, String clientName) {
        this.ctiRequires = ctiRequires;
        this.loopGroup = loopGroup;
        this.ipAddress = ipAddress;
        this.port = port;
        this.clientName = clientName;
    }

    public static Props props(CtiRequires ctiRequires, EventLoopGroup loopGroup, String ipAddress, int port, String clientName) {
        return Props.create(ConnectionActor.class, () -> {
            return new ConnectionActor(ctiRequires, loopGroup, ipAddress, port, clientName);
        });
    }

    class initialState {
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
            nettyChannel.sendLogin();
            getContext().become(new WaitingLoginResp().createReceive());
        }

        private void onFailed(Throwable throwable) {
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(r -> System.out.println("unexpected message : " + r))
                .build();
    }

    @Override
    public void connected(NettyChannel nettyChannel) {
        self().tell(nettyChannel, ActorRef.noSender());
    }

    @Override
    public void disconnected(NettyChannel nettyChannel) {
        self().tell(nettyChannel, ActorRef.noSender());
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
    public void preStart() throws Exception {
        var f = NettyChannel.newConnection(loopGroup, this, ipAddress, port);
        f.whenComplete((nettyChannel, throwable) -> {
            if (throwable != null) {
                self().tell(throwable, ActorRef.noSender());
            }
        });
    }

    private class WaitingLoginResp {

        public Receive createReceive() {
            return null;
        }
    }
}
