package com.uangel.svc.biz.impl.callactor;

import com.uangel.svc.biz.actorutil.ResponseType;
import com.uangel.svc.biz.call.InboundCall;
import com.uangel.svc.biz.call.InboundCallResp;

class messageInboundCall implements ResponseType<InboundCallResp> {
    String mdn;
    String calledNum;
    InboundCall inboundCall;

    messageInboundCall(String mdn, String calledNum, InboundCall inboundCall) {
        this.mdn = mdn;
        this.calledNum = calledNum;
        this.inboundCall = inboundCall;
    }
}
