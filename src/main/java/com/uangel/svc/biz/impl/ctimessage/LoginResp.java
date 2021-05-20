package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LoginResp extends HasCallID {

    private final Optional<String> IServerVer;
    private final String result;
    private final String status;

    public LoginResp(String callID, Optional<String> IServerVer, String Result, String Status) {
        super(callID);
        this.IServerVer = IServerVer;
        result = Result;
        status = Status;
    }

    @Override
    public String messageType() {
        return "LoginResp";
    }


    public Optional<String> getIServerVer() {
        return IServerVer;
    }

    public String getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public void MarshalXml(GctiMsgWriter writer) {

        this.writeCallID(writer);

        writer.writeElement(messageType(), xmlWriter1 -> {
            xmlWriter1.writeAttribute("IServerVer", getIServerVer());
            xmlWriter1.writeAttribute("Result", getResult());
            xmlWriter1.writeAttribute("Status", getStatus());
        });
    }

    static public MessageUnmarshaller Unmarshaller() {
        return new MessageUnmarshaller() {
            @Override
            void attr(Attributes attr) {
                var IserverVer = Optional.ofNullable(attr.getValue("IServerVer"))
                    .map(String::trim);

            var Result = Try.fromOptional(Optional.ofNullable(attr.getValue("Result"))
                    .map(String::trim));

            var Status = Try.fromOptional(Optional.ofNullable(attr.getValue("Status"))
                    .map(String::trim));

            parseCallID(callID -> Try.from(() -> new LoginResp( callID, IserverVer, Result.get() , Status.get() )));
            }
        };
    }
}