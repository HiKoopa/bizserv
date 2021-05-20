package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;

import java.util.Optional;

public class NewCall extends HasCallID {

    String calledNum;

    public NewCall(String callID, String calledNum) {
        super(callID);
        this.calledNum = calledNum;
    }

    @Override
    public String messageType() {
        return "NewCall";
    }

    @SuppressWarnings("CodeBlock2Expr")
    @Override
    public void MarshalXml(GctiMsgWriter writer) {
        this.writeCallID(writer);
        writer.writeElement(messageType(), gctiMsgWriter -> {
            gctiMsgWriter.writeAttribute("CallControlMode", "Network");
            gctiMsgWriter.writeAttribute("Version", "4.0");

            gctiMsgWriter.writeElement("CalledNum", gctiMsgWriter1 -> {
                gctiMsgWriter1.writeText(calledNum);
            });
        });
    }

    public String getCalledNum() {
        return calledNum;
    }

    static public Unmarshaller<String> CalledNumParser() {
        return new Unmarshaller<>() {
            void text(String txt) {
                success(txt.trim());
            }
        };
    }

    @SuppressWarnings("CodeBlock2Expr")
    static public MessageUnmarshaller Unmarshaller() {
        return new MessageUnmarshaller() {
            @Override
            void elem(String qName, Attributes attr) {
                if ("CalledNum".equals(qName)) {
                    become(CalledNumParser(), elementHandler -> {
                        parseCallID(callID -> elementHandler.result().map(called -> {
                            return new NewCall(callID, called);
                        }));
                    });
                }
            }
        };
    }
}
