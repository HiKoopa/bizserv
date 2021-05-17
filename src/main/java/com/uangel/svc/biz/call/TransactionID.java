package com.uangel.svc.biz.call;

public class TransactionID {
    private String jobID;
    private String transactionID;

    public TransactionID(String jobID, String transactionID) {
        this.jobID = jobID;
        this.transactionID = transactionID;
    }

    public String getJobID() {
        return jobID;
    }

    public String getTransactionID() {
        return transactionID;
    }
}
