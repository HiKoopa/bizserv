package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.impl.ctimessage.CtiMessage;

public interface CtiMessageHandler {
    void handleMessage(CtiMessage msg);
}
