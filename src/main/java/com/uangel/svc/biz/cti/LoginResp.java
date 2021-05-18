package com.uangel.svc.biz.cti;

import com.uangel.svc.biz.cti.CtiMessage;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LoginResp implements CtiMessage {

    private final String callID;
    private final Optional<String> IServerVer;
    private final String result;
    private final String status;

    public LoginResp(String callID, Optional<String> IServerVer, String Result, String Status) {
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

    public String getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }
}
