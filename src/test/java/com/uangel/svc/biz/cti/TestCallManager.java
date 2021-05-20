package com.uangel.svc.biz.cti;

import com.uangel.svc.biz.call.CallManager;
import com.uangel.svc.biz.call.InboundCall;
import com.uangel.svc.biz.impl.ctinetty.CtiConnection;
import com.uangel.svc.biz.impl.ctinetty.CtiRequires;
import com.uangel.svc.biz.impl.ctisim.CtiServerFactory;
import com.uangel.svc.biz.impl.ctisim.ServiceLogic;
import com.uangel.svc.biz.modules.ActorSystemModule;
import com.uangel.svc.biz.modules.CallManagerModule;
import com.uangel.svc.biz.modules.NettyCtiClientModule;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ActorSystemModule.class, CallManagerModule.class, NettyCtiClientModule.class})
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
public class TestCallManager {

    @Inject
    CallManager callManager;

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
                var res = callManager.InboundCall("10101", "10005", new InboundCall("transID", "Jobid"));

                loginReqPromise.get(4, TimeUnit.SECONDS);
                newCallPromise.get(4, TimeUnit.SECONDS);

                res.get(4 , TimeUnit.SECONDS);

            } finally {
                group.shutdownGracefully();
            }
        }

    }
}
