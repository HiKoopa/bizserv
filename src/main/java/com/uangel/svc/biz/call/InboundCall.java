package com.uangel.svc.biz.call;

public class InboundCall {
    private String transactionID;
    private String jobID;

    public InboundCall(String transactionID, String jobID) {
        this.transactionID = transactionID;
        this.jobID = jobID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public String getJobID() {
        return jobID;
    }
}
