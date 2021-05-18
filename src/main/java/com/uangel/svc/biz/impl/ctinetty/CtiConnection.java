package com.uangel.svc.biz.impl.ctinetty;

import akka.actor.ActorRef;
import com.uangel.svc.biz.actorutil.ResponseType;
import com.uangel.svc.biz.cti.CallStatusListener;
import com.uangel.svc.biz.cti.CtiClient;
import com.uangel.svc.biz.cti.UDataSet;
import io.netty.channel.EventLoopGroup;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class CtiConnection implements CtiClient {

    private String ipAddress;
    private int port;
    private String clientName;
    private ActorRef actorRef;

    public CtiConnection(CtiRequires ctiRequires, EventLoopGroup loopGroup, String ipAddress, int port, String clientName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.clientName = clientName;

        actorRef = ctiRequires.getActorSystem().actorOf(ConnectionActor.props(ctiRequires, loopGroup, ipAddress, port, clientName));
    }

    @Override
    public CompletableFuture<Void> NewCall(String CalledNum, String CallID) {
        return ResponseType.askFor(actorRef, new ConnectionActor.messageNewCall(CallID, CalledNum), Duration.ofSeconds(10));
    }

    @Override
    public CompletableFuture<Void> CallInfoReq(String CalledNum, String CallID) {
        return null;
    }

    @Override
    public CompletableFuture<Void> UDataSet(String CalledNum, String CallID, UDataSet uDataSet) {
        return null;
    }

    @Override
    public CompletableFuture<Void> UDataGet(String CalledNum, String CallID, String keys, String requestID) {
        return null;
    }

    @Override
    public CompletableFuture<Void> EndCall(String CalledNum, String CallID, String endCause) {
        return null;
    }

    @Override
    public void AddHandler(CallStatusListener listener) {

    }
}
