package com.uangel.svc.biz.impl.ctisim;

import com.uangel.svc.biz.impl.ctimessage.LoginReq;
import com.uangel.svc.biz.impl.ctimessage.NewCall;
import io.netty.channel.Channel;

public interface ServiceLogic {
    void onLoginReq(Channel channel,  LoginReq req);
    void onNewCall(Channel channel, NewCall newcall);
}
