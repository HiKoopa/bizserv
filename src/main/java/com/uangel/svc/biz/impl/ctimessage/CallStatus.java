package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.functional.Try;
import org.xml.sax.Attributes;

import java.util.Optional;

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

    @SuppressWarnings("CodeBlock2Expr")
    static public MessageUnmarshaller Unmarshaller() {
        return new MessageUnmarshaller() {
            @Override
            void attr(Attributes attr) {
                parseCallID(callID -> {
                    return Try.fromOptional(Optional.ofNullable(attr.getValue("Event"))).map(e -> {
                        return new CallStatus(callID, e);
                    });
                });
            }
        };
    }
}
