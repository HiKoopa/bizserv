package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CallStatusListener;
import com.uangel.svc.biz.cti.CtiClient;
import com.uangel.svc.biz.cti.UDataSet;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.CompletableFuture;

public class HAConnection implements CtiClient {

    private CtiConnection conn1;
    private CtiConnection conn2;

    public HAConnection(CtiRequires ctiRequires, EventLoopGroup loopGroup, String ipAddress1, int port1, String ipAddress2, int port2, String clientName) {
        conn1 = new CtiConnection(ctiRequires, loopGroup, ipAddress1, port1, clientName);
        conn2 = new CtiConnection(ctiRequires, loopGroup, ipAddress2, port2, clientName);
    }

    public <T> CompletableFuture<T> OneOf(CompletableFuture<T> f1, CompletableFuture<T> f2) {
        CompletableFuture<T> promise = new CompletableFuture<>();
        f1.whenComplete((unused, throwable) -> {
            if (throwable != null) {
                f2.whenComplete((unused1, throwable1) -> {
                    if (throwable1 != null) {
                        promise.completeExceptionally(new Exception("TODO"));
                    } else {
                        promise.complete(unused1);
                    }
                });
            } else {
                promise.complete(unused);
            }
        });

        f2.whenComplete((unused, throwable) -> {
            if (throwable == null) {
                promise.complete(unused);
            }
        });
        return promise;
    }

    @Override
    public CompletableFuture<Void> NewCall(String CalledNum, String CallID) {
        var f1 = conn1.NewCall(CalledNum, CallID);
        var f2 = conn2.NewCall(CalledNum, CallID);

        return OneOf(f1, f2);
    }

    @Override
    public CompletableFuture<Void> CallInfoReq(String CalledNum, String CallID) {
        var f1 = conn1.CallInfoReq(CalledNum, CallID);
        var f2 = conn2.CallInfoReq(CalledNum, CallID);

        return OneOf(f1, f2);
    }

    @Override
    public CompletableFuture<Void> UDataSet(String CalledNum, String CallID, UDataSet uDataSet) {
        var f1 = conn1.UDataSet(CalledNum, CallID, uDataSet);
        var f2 = conn2.UDataSet(CalledNum, CallID, uDataSet);

        return OneOf(f1, f2);
    }

    @Override
    public CompletableFuture<Void> UDataGet(String CalledNum, String CallID, String keys, String requestID) {
        var f1 = conn1.UDataGet(CalledNum, CallID, keys, requestID);
        var f2 = conn2.UDataGet(CalledNum, CallID, keys, requestID);

        return OneOf(f1, f2);
    }

    @Override
    public CompletableFuture<Void> EndCall(String CalledNum, String CallID, String endCause) {
        var f1 = conn1.EndCall(CalledNum, CallID, endCause);
        var f2 = conn2.EndCall(CalledNum, CallID, endCause);

        return OneOf(f1, f2);
    }

    @Override
    public void AddHandler(CallStatusListener listener) {
        conn1.AddHandler(listener);
        conn2.AddHandler(listener);
    }
}
