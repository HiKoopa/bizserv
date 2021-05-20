package com.uangel.svc.biz.impl.callactor;

import com.uangel.svc.biz.cti.UDataResp;

class messageCallUDataResp implements hasCallID{
    String callID;
    UDataResp uDataResp;

    messageCallUDataResp(String callID, UDataResp uDataResp) {
        this.callID = callID;
        this.uDataResp = uDataResp;
    }

    @Override
    public String getCallID() {
        return callID;
    }
}
