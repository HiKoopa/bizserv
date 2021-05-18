package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CallStatusListener;
import com.uangel.svc.biz.cti.CtiClient;
import com.uangel.svc.biz.cti.UDataSet;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import scala.util.control.TailCalls;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CtiRouter implements CtiClient, AutoCloseable {

    List<CtiRoutingInfo> ctiRoutingInfoList = new ArrayList<>();
    EventLoopGroup group = new NioEventLoopGroup();

    //TODO Configable
    @Inject
    public CtiRouter(CtiRequires ctiRequires) {
        var l = new ArrayList<String>();
        l.add("100");
        ctiRoutingInfoList.add(new CtiRoutingInfo(l, new HAConnection(ctiRequires, group, "127.0.0.1", 7009, "127.0.0.1", 7010, "NUGUIVR")));
    }

    @SuppressWarnings("OptionalIsPresent")
    public CompletableFuture<CtiRoutingInfo> SearchRoute(String CalledNum) {
         var o = ctiRoutingInfoList.stream().filter(c -> c.hasPrefix(CalledNum)).findFirst();

        if (o.isPresent()) {
            return CompletableFuture.completedFuture(o.get());
        } else {
            return CompletableFuture.failedFuture(new Exception("TODO"));
        }
    }

    @Override
    public CompletableFuture<Void> NewCall(String CalledNum, String CallID) {
        return SearchRoute(CalledNum).thenCompose(c -> c.getHaConnection().NewCall(CalledNum, CallID));
    }

    @Override
    public CompletableFuture<Void> CallInfoReq(String CalledNum, String CallID) {
        return SearchRoute(CalledNum).thenCompose(c -> c.getHaConnection().CallInfoReq(CalledNum, CallID));
    }

    @Override
    public CompletableFuture<Void> UDataSet(String CalledNum, String CallID, UDataSet uDataSet) {
        return SearchRoute(CalledNum).thenCompose(c -> c.getHaConnection().UDataSet(CalledNum, CallID, uDataSet));
    }

    @Override
    public CompletableFuture<Void> UDataGet(String CalledNum, String CallID, String keys, String requestID) {
        return SearchRoute(CalledNum).thenCompose(c -> c.getHaConnection().UDataGet(CalledNum, CallID, keys, requestID));
    }

    @Override
    public CompletableFuture<Void> EndCall(String CalledNum, String CallID, String endCause) {
        return SearchRoute(CalledNum).thenCompose(c -> c.getHaConnection().EndCall(CalledNum, CallID, endCause));
    }

    @Override
    public void AddHandler(CallStatusListener listener) {
        ctiRoutingInfoList.forEach(c -> c.getHaConnection().AddHandler(listener));
    }

    @Override
    public void close() {
        group.shutdownGracefully();
    }
}
