package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;

import java.util.Optional;

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

    static public MessageUnmarshaller Unmarshaller() {
        return new MessageUnmarshaller() {
            @Override
            void attr(Attributes attr) {
                var clientName = Try.fromOptional(Optional.ofNullable(attr.getValue("ClientName")));
                parseCallID(callID -> clientName.map(cn -> new LoginReq( callID, cn)));
            }
        };
    }
}
