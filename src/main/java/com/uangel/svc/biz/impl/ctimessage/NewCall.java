package com.uangel.svc.biz.impl.ctimessage;

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
}
