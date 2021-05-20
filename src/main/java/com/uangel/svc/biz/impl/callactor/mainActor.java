package com.uangel.svc.biz.impl.callactor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.uangel.svc.biz.call.TransactionID;

class mainActor extends AbstractActor {
    private CallManagerRequires requires;

    public mainActor(CallManagerRequires requires) {
        this.requires = requires;
    }

    static Props props(CallManagerRequires requires) {
        return Props.create(mainActor.class, () -> new mainActor(requires));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(messageInboundCall.class, this::onInboundCall)
                .match(messageConversationResult.class, this::onConversationCall)
                .match(hasCallID.class, this::onCallIDMsg)
                .build();
    }

    private void onCallIDMsg(hasCallID msg) {
        var childOpt = this.getContext().findChild(msg.getCallID());
        childOpt.orElseGet(() -> this.context().actorOf(childActor.props(requires, msg.getCallID()), msg.getCallID())).forward(msg, getContext());
    }

    private void onConversationCall(messageConversationResult msg) {
        var ref = getChild(msg.transactionID);
        ref.forward(msg, getContext());
    }

    private void onInboundCall(messageInboundCall msg) {
        var ref = getChild(new TransactionID(msg.inboundCall.getJobID(), msg.inboundCall.getTransactionID()));
        ref.forward(msg, getContext());
    }

    ActorRef getChild(TransactionID transactionID) {
        var childName = String.format("%s-%s", transactionID.getJobID(), transactionID.getTransactionID());
        var childOpt = this.getContext().findChild(childName);
        var callID = childName;

        return childOpt.orElseGet(() -> this.context().actorOf(childActor.props(requires, callID), childName));
    }
}
