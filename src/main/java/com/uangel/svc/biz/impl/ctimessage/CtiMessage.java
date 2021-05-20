package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;

public interface CtiMessage {
    String messageType();
    void MarshalXml(GctiMsgWriter writer);
    default Try<byte[]> toXML() {
        var writer = new GctiMsgWriter();
        MarshalXml(writer);
        return writer.toBytes();
    }
}
