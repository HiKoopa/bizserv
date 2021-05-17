package com.uangel.svc.biz.cti;

public class CallInfoResp {
    private String ani;
    private String dnis;
    private String calledNum;
    private String connID;
    private String tsCallID;
    private String portDn;
    private String portQueue;
    private String otherDn;
    private String lastEvent;

    public String getAni() {
        return ani;
    }

    public String getDnis() {
        return dnis;
    }

    public String getCalledNum() {
        return calledNum;
    }

    public String getConnID() {
        return connID;
    }

    public String getTsCallID() {
        return tsCallID;
    }

    public String getPortDn() {
        return portDn;
    }

    public String getPortQueue() {
        return portQueue;
    }

    public String getOtherDn() {
        return otherDn;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setAni(String ani) {
        this.ani = ani;
    }

    public void setDnis(String dnis) {
        this.dnis = dnis;
    }

    public void setCalledNum(String calledNum) {
        this.calledNum = calledNum;
    }

    public void setConnID(String connID) {
        this.connID = connID;
    }

    public void setTsCallID(String tsCallID) {
        this.tsCallID = tsCallID;
    }

    public void setPortDn(String portDn) {
        this.portDn = portDn;
    }

    public void setPortQueue(String portQueue) {
        this.portQueue = portQueue;
    }

    public void setOtherDn(String otherDn) {
        this.otherDn = otherDn;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }
}
