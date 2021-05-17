package com.uangel.svc.biz.cti;

public class Node {
    private String name;
    private String tpe;
    private String val;

    public Node(String name, String tpe, String val) {
        this.name = name;
        this.tpe = tpe;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public String getTpe() {
        return tpe;
    }

    public String getVal() {
        return val;
    }
}
