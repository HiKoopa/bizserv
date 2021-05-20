package com.uangel.svc.biz.impl.callactor;

import com.uangel.svc.biz.cti.CallInfoResp;

class messageCallInfoResp implements hasCallID{
    String callID;
    CallInfoResp callInfoResp;

    messageCallInfoResp(String callID, CallInfoResp callInfoResp) {
        this.callID = callID;
        this.callInfoResp = callInfoResp;
    }

    @Override
    public String getCallID() {
        return callID;
    }
}
