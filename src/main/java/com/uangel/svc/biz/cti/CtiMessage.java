package com.uangel.svc.biz.cti;

import com.uangel.svc.biz.actorutil.Try;

public interface CtiMessage {
    String messageType();
    void MarshalXml(GctiMsgWriter writer);

    default Try<byte[]> toXML() {
        var writer = new GctiMsgWriter();
        MarshalXml(writer);
        return writer.toBytes();
    }
}
