package com.uangel.svc.biz.impl.ctinetty;

import java.util.Optional;

public class LoginResp implements CtiMessage {

    private String callID;
    private Optional<String> IServerVer;
    private Optional<String> result;
    private Optional<String> status;

    public LoginResp(String callID, Optional<String> IServerVer, Optional<String> Result, Optional<String> Status) {
        this.callID = callID;
        this.IServerVer = IServerVer;
        result = Result;
        status = Status;
    }

    @Override
    public String messageType() {
        return "LoginResp";
    }

    public String getCallID() {
        return callID;
    }

    public Optional<String> getIServerVer() {
        return IServerVer;
    }

    public Optional<String> getResult() {
        return result;
    }

    public Optional<String> getStatus() {
        return status;
    }
}
