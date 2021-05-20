package com.uangel.svc.biz.impl.ctimessage;

public class LoginReq extends HasCallID {

    String clientName;

    public LoginReq(String callID, String clientName) {
        super(callID);
        this.clientName = clientName;
    }

    @Override
    public String messageType() {
        return "LoginReq";
    }

    @Override
    public void MarshalXml(GctiMsgWriter writer) {
        this.writeCallID(writer);
        writer.writeElement(messageType(), gctiMsgWriter -> {
            gctiMsgWriter.writeAttribute("ClientName", clientName);
            gctiMsgWriter.writeAttribute("ReportStatus", "true");
            gctiMsgWriter.writeAttribute("ServerMonitor", "clear");
        });
    }

    public String getClientName() {
        return clientName;
    }
}
