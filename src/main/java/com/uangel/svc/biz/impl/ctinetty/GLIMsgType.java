package com.uangel.svc.biz.impl.ctinetty;

public enum GLIMsgType {
    GLI_MSG_TYPE_KEEP_ALIVE_REQ (0),
    GLI_MSG_TYPE_KEEP_ALIVE_ACK (1),
    GLI_MSG_TYPE_ERROR_ACK (2),
    GLI_MSG_TYPE_XML_DATA (3);

    private int value;

    GLIMsgType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


