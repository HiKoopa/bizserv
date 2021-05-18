package com.uangel.svc.biz.impl.ctinetty;

import java.util.Optional;

public enum GLIMsgType {
    GLI_MSG_TYPE_KEEP_ALIVE_REQ (0),
    GLI_MSG_TYPE_KEEP_ALIVE_ACK (1),
    GLI_MSG_TYPE_ERROR_ACK (2),
    GLI_MSG_TYPE_XML_DATA (3);

    private int value;

    GLIMsgType(int value) {
        this.value = value;
    }

    static Optional<GLIMsgType> valueOf(int value) {
        var a = GLIMsgType.values();
        for (int i = 0; i < a.length; i++) {
            if (a[i].getValue() == value) {
                return Optional.of(a[i]);
            }
        }
        return Optional.empty();
    }

    public int getValue() {
        return value;
    }
}


