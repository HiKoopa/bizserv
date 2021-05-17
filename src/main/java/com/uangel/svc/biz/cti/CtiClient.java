package com.uangel.svc.biz.cti;

import java.util.concurrent.CompletableFuture;

public interface CtiClient {
    CompletableFuture<Void> NewCall(String CallID, String CalledNum);
    CompletableFuture<Void> CallInfoReq(String CallID);
    CompletableFuture<Void> UDataSet(String CallID, UDataSet uDataSet);
    CompletableFuture<Void> UDataGet(String CallID, String keys, String requestID);
    CompletableFuture<Void> EndCall(String CallID, String endCause);

    void AddHandler( CallStatusListener listener );
}
