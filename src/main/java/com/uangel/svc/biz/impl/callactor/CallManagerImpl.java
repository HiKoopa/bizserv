package com.uangel.svc.biz.impl.callactor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.uangel.svc.biz.actorutil.ResponseType;
import com.uangel.svc.biz.call.CallManager;
import com.uangel.svc.biz.call.InboundCall;
import com.uangel.svc.biz.call.InboundCallResp;
import com.uangel.svc.biz.call.TransactionID;
import com.uangel.svc.biz.cti.CallInfoResp;
import com.uangel.svc.biz.cti.CallStatusListener;
import com.uangel.svc.biz.cti.UDataResp;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

//TODO impl을 다른 이름으로 바꿀것
public class CallManagerImpl implements CallManager, CallStatusListener {
    private ActorRef actorRef;

    @Inject
    public CallManagerImpl(ActorSystem actorSystem, CallManagerRequires requires) {
        this.actorRef = actorSystem.actorOf(mainActor.props(requires), "biz-callmanager");
        requires.ctiClient.AddHandler(this);
    }

    @Override
    public CompletableFuture<InboundCallResp> InboundCall(String mdn, String calledNum, InboundCall inboundCall) {
        return ResponseType.askFor(actorRef, new messageInboundCall(mdn, calledNum, inboundCall), Duration.ofSeconds(5));
    }

    @Override
    public CompletableFuture<Void> ConversationResult(TransactionID transactionID) {
        return ResponseType.askFor(actorRef, new messageConversationResult(transactionID), Duration.ofSeconds(5));
    }

    @Override
    public void HandleCallStatus(String callID, String event) {
        actorRef.tell(new messageCallStatus(callID, event), ActorRef.noSender());
    }

    @Override
    public void HandleCallInfoResp(String callID, CallInfoResp callInfoResp) {
        actorRef.tell(new messageCallInfoResp(callID, callInfoResp), ActorRef.noSender());
    }

    @Override
    public void HandleUDataResp(String callID, UDataResp uDataResp) {
        actorRef.tell(new messageCallUDataResp(callID, uDataResp), ActorRef.noSender());
    }
}
