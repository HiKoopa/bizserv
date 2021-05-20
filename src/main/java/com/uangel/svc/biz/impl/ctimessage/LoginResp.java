package com.uangel.svc.biz.impl.ctimessage;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LoginResp extends HasCallID {

    private final Optional<String> IServerVer;
    private final String result;
    private final String status;

    public LoginResp(String callID, Optional<String> IServerVer, String Result, String Status) {
        super(callID);
        this.IServerVer = IServerVer;
        result = Result;
        status = Status;
    }

    @Override
    public String messageType() {
        return "LoginResp";
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

    public void MarshalXml( GctiMsgWriter writer ) {

        this.writeCallID(writer);

        writer.writeElement(messageType(), xmlWriter1 -> {
            xmlWriter1.writeAttribute("IServerVer", getIServerVer());
            xmlWriter1.writeAttribute("Result", getResult());
            xmlWriter1.writeAttribute("Status", getStatus());
        });
    }
}
