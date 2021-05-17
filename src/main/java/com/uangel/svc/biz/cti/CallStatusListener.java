package com.uangel.svc.biz.cti;

public interface CallStatusListener {
    void HandleCallStatus(String callID, String event);
    void HandleCallInfoResp(String callID, CallInfoResp callInfoResp);
    void HandleUDataResp(String callID, UDataResp uDataResp);
}
