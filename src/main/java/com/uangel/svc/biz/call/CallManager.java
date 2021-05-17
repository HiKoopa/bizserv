package com.uangel.svc.biz.call;

import java.util.concurrent.CompletableFuture;

public interface CallManager {
    CompletableFuture<InboundCallResp> InboundCall(String mdn, String calledNum, InboundCall inboundCall);
    CompletableFuture<Void> ConversationResult(TransactionID transactionID);
}
