package com.uangel.svc.biz.impl.callactor;

import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import com.uangel.svc.biz.call.InboundCallResp;

public class childActor extends AbstractActorWithStash {
    private CallManagerRequires requires;
    private String callID;

    public childActor(CallManagerRequires requires, String callID) {
        this.requires = requires;
        this.callID = callID;
    }

    public static Props props(CallManagerRequires requires, String callID) {
        return Props.create(childActor.class, () -> new childActor(requires, callID));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(messageInboundCall.class, this::onInboundCall)
                .build();
    }

    private void onInboundCall(messageInboundCall msg) {
        requires.ctiClient.NewCall(msg.calledNum, callID);
        msg.sendResponse(sender(), new InboundCallResp(), self());
    }
}
