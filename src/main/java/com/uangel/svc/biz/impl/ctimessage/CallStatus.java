package com.uangel.svc.biz.impl.ctimessage;

public class CallStatus extends HasCallID{

    String event;
    public CallStatus(String callID, String event) {
        super(callID);
        this.event = event;
    }

    @Override
    public String messageType() {
        return "CallStatus";
    }

    public String getEvent() {
        return event;
    }

    @SuppressWarnings("CodeBlock2Expr")
    @Override
    public void MarshalXml(GctiMsgWriter writer) {
        this.writeCallID(writer);
        writer.writeElement(messageType(), gctiMsgWriter -> {
            gctiMsgWriter.writeAttribute("Event", event);
        });
    }
}
