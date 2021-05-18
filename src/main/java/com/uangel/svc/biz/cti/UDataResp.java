package com.uangel.svc.biz.cti;

import java.util.List;

public class UDataResp implements CtiMessage{
    private String result;

    private String requestID;
    private List<Node> UDataEx;

    public UDataResp(String result, String requestID, List<Node> UDataEx) {
        this.result = result;
        this.requestID = requestID;
        this.UDataEx = UDataEx;
    }

    public String getResult() {
        return result;
    }

    public String getRequestID() {
        return requestID;
    }

    public List<Node> getUDataEx() {
        return UDataEx;
    }

    @Override
    public String messageType() {
        return "UDataResp";
    }
}
