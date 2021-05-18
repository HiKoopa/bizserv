package com.uangel.svc.biz.cti;

import java.util.concurrent.CompletableFuture;

public interface CtiClient {
    CompletableFuture<Void> NewCall(String CalledNum, String CallID);
    CompletableFuture<Void> CallInfoReq(String CalledNum, String CallID);
    CompletableFuture<Void> UDataSet(String CalledNum, String CallID, UDataSet uDataSet);
    CompletableFuture<Void> UDataGet(String CalledNum, String CallID, String keys, String requestID);
    CompletableFuture<Void> EndCall(String CalledNum, String CallID, String endCause);

    void AddHandler( CallStatusListener listener );
}
