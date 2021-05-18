package com.uangel.svc.biz.cti;

import java.util.List;

public class UDataSet implements CtiMessage{
    private String action;

    private String requestID;
    private List<Node> UDataEx;

    public String getAction() {
        return action;
    }

    public String getRequestID() {
        return requestID;
    }

    public List<Node> getUDataEx() {
        return UDataEx;
    }

    public UDataSet(String action, String requestID, List<Node> UDataEx) {
        this.action = action;
        this.requestID = requestID;
        this.UDataEx = UDataEx;
    }

    @Override
    public String messageType() {
        return "UDataSet";
    }
}
