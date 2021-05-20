package com.uangel.svc.biz.cti;

import akka.actor.ActorSystem;
import com.uangel.svc.biz.impl.ctimessage.LoginReq;
import com.uangel.svc.biz.impl.ctimessage.LoginResp;
import com.uangel.svc.biz.impl.ctimessage.NewCall;
import com.uangel.svc.biz.impl.ctinetty.CtiConnection;
import com.uangel.svc.biz.impl.ctinetty.CtiRequires;
import com.uangel.svc.biz.impl.ctinetty.CtiRouter;
import com.uangel.svc.biz.impl.ctisim.CtiServerFactory;
import com.uangel.svc.biz.impl.ctisim.ServiceLogic;
import com.uangel.svc.biz.modules.ActorSystemModule;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ActorSystemModule.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)

public class TestCtiClient {

    @Autowired
    ActorSystem actorSystem;

    @Test
    public void Test() throws ExecutionException, InterruptedException, TimeoutException {

        var loginReqPromise = new CompletableFuture<Boolean>();
        var newCallPromise = new CompletableFuture<Boolean>();

        try(var sf = new CtiServerFactory()) {
            sf.newServer(7009, new ServiceLogic() {
                @Override
                public void onLoginReq(Channel channel, LoginReq req) {
                    loginReqPromise.complete(true);
                    channel.writeAndFlush(new LoginResp(req.getCallID(), Optional.of("IVR Server:8.1.000.05"), "Success", "OK"));
                }
                @Override
                public void onNewCall(Channel channel, NewCall newcall) {
                    newCallPromise.complete(true);
                }
            });
            var group = new NioEventLoopGroup();
            try {
                var client = new CtiConnection(new CtiRequires(actorSystem), group, "127.0.0.1", 7009, "NUGUIVR");


                var res = client.NewCall("10005", "hello");

                loginReqPromise.get(4, TimeUnit.SECONDS);
                newCallPromise.get(4, TimeUnit.SECONDS);

                res.get(4 , TimeUnit.SECONDS);

            } finally {
                group.shutdownGracefully();
            }
        }
    }

    @Test
    public void Test1() throws ExecutionException, InterruptedException, TimeoutException {
        var loginReqPromise = new CompletableFuture<Boolean>();
        var newCallPromise = new CompletableFuture<Boolean>();

        var loginReqPromise2 = new CompletableFuture<Boolean>();
        var newCallPromise2 = new CompletableFuture<Boolean>();

        try(var sf = new CtiServerFactory()) {
            sf.newServer(7009, new ServiceLogic() {
                @Override
                public void onLoginReq(Channel channel, LoginReq req) {
                    loginReqPromise.complete(true);
                    channel.writeAndFlush(new LoginResp(req.getCallID(), Optional.of("IVR Server:8.1.000.05"), "Success", "OK"));
                }
                @Override
                public void onNewCall(Channel channel, NewCall newcall) {
                    newCallPromise.complete(true);
                }
            });

            sf.newServer(7010, new ServiceLogic() {
                @Override
                public void onLoginReq(Channel channel, LoginReq req) {
                    loginReqPromise2.complete(true);
                    channel.writeAndFlush(new LoginResp(req.getCallID(), Optional.of("IVR Server:8.1.000.05"), "Success", "OK"));
                }
                @Override
                public void onNewCall(Channel channel, NewCall newcall) {
                    newCallPromise2.complete(true);
                }
            });
            var group = new NioEventLoopGroup();
            try {
                var client = new CtiRouter(new CtiRequires(actorSystem));
                var res = client.NewCall("10005", "hello");

                loginReqPromise.get(4, TimeUnit.SECONDS);
                newCallPromise.get(4, TimeUnit.SECONDS);

                loginReqPromise2.get(4, TimeUnit.SECONDS);
                newCallPromise2.get(4, TimeUnit.SECONDS);

                res.get(4 , TimeUnit.SECONDS);

            } finally {
                group.shutdownGracefully();
            }
        }

    }
}
