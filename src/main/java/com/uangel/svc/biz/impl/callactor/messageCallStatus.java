package com.uangel.svc.biz.impl.callactor;

class messageCallStatus implements hasCallID {
    String callID;
    String event;

    messageCallStatus(String callID, String event) {
        this.callID = callID;
        this.event = event;
    }

    @Override
    public String getCallID() {
        return callID;
    }
}
