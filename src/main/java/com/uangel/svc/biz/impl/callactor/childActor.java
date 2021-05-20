package com.uangel.svc.biz.impl.callactor;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.uangel.svc.biz.call.InboundCallResp;
import com.uangel.svc.biz.impl.ctimessage.CallStatus;

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
        getContext().become(new WaitingCallStatusState(msg, sender()).createReceive());
    }

    private class WaitingCallStatusState {
        private messageInboundCall msg;
        private ActorRef sender;

        public WaitingCallStatusState(messageInboundCall msg, ActorRef sender) {
            this.msg = msg;
            this.sender = sender;
        }

        private void onCallStatus(messageCallStatus status) {
            msg.sendResponse(sender, new InboundCallResp(), self());
        }

        public Receive createReceive() {
            return receiveBuilder()
                    .match(messageCallStatus.class, this::onCallStatus)
                    .build();
        }
    }
}
