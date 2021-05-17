package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CallStatusListener;
import com.uangel.svc.biz.cti.CtiClient;
import com.uangel.svc.biz.cti.UDataSet;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class NettyCtiRouter implements CtiClient {

    @Inject
    public NettyCtiRouter() {
    }

    @Override
    public CompletableFuture<Void> NewCall(String CallID, String CalledNum) {
        return null;
    }

    @Override
    public CompletableFuture<Void> CallInfoReq(String CallID) {
        return null;
    }

    @Override
    public CompletableFuture<Void> UDataSet(String CallID, UDataSet uDataSet) {
        return null;
    }

    @Override
    public CompletableFuture<Void> UDataGet(String CallID, String keys, String requestID) {
        return null;
    }

    @Override
    public CompletableFuture<Void> EndCall(String CallID, String endCause) {
        return null;
    }

    @Override
    public void AddHandler(CallStatusListener listener) {

    }
}
