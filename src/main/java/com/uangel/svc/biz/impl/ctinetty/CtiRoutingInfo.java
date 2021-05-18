package com.uangel.svc.biz.impl.ctinetty;

import java.util.List;

public class CtiRoutingInfo {
    private final List<String> prefixList;
    private final HAConnection haConnection;

    public CtiRoutingInfo(List<String> prefixList, HAConnection haConnection) {
        this.prefixList = prefixList;
        this.haConnection = haConnection;
    }

    public boolean hasPrefix(String calledNum) {
        return prefixList.stream().anyMatch(calledNum::startsWith);
    }

    public HAConnection getHaConnection() {
        return haConnection;
    }
}
