package com.uangel.svc.biz.impl.callactor;

import com.uangel.svc.biz.actorutil.ResponseType;
import com.uangel.svc.biz.call.TransactionID;

class messageConversationResult implements ResponseType<Void> {
    TransactionID transactionID;

    messageConversationResult(TransactionID transactionID) {
        this.transactionID = transactionID;
    }
}
