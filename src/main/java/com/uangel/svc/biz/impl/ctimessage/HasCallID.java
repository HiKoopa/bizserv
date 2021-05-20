package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;

public abstract class HasCallID implements CtiMessage {

    private String callID;

    public HasCallID(String callID) {
        this.callID = callID;
    }

    public String getCallID() {
        return callID;
    }

    @SuppressWarnings("CodeBlock2Expr")
    public void writeCallID(GctiMsgWriter writer) {
        writer.writeElement("CallId", xmlWriter1 -> {
            xmlWriter1.writeText(getCallID());
        });
    }
}
