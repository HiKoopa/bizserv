package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.SupplierEx;
import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

class CtiMessageUnmarshaller extends Unmarshaller<CtiMessage> {

    private static Throwable noMessage = new NoSuchElementException("");


    Try<CtiMessage> ret = Try.Failure(noMessage);
    @SuppressWarnings("CodeBlock2Expr")
    public CtiMessageUnmarshaller(UnmarshallerStack stack) {
        setStack(stack);
        become(new RootParser(), rootParser -> {
            complete(rootParser.result());
        });
    }


    @SuppressWarnings("CodeBlock2Expr")


    private class RootParser extends Unmarshaller<CtiMessage> {


        @SuppressWarnings("CodeBlock2Expr")
        @Override
        public void elem(String qName, Attributes attributes)  {
            if (qName.equals("GctiMsg")) {
                become(new GctiParser(), gctiParser -> {
                    complete(gctiParser.result());
                });
            }
        }


    }



    static Map<String, MessageUnmarshaller> messageParser = new HashMap<>();

    static {
        messageParser.put("LoginResp", LoginResp.Unmarshaller());

        messageParser.put("LoginReq", LoginReq.Unmarshaller());

        messageParser.put("CallStatus", CallStatus.Unmarshaller());
        messageParser.put("NewCall", NewCall.Unmarshaller());

    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CodeBlock2Expr"})
    private class GctiParser extends Unmarshaller<CtiMessage> {
        Optional<String> callID = Optional.empty();
        Try<PartialMessage> partialMessage = Try.Failure(noMessage);

        @Override
        public void elem(String qName, Attributes attributes)  {
            if ("CallId".equals(qName)) {
                become(new CallIdParser(), callIdParser -> {
                    callID = callIdParser.result().toOptional();
                });
            } else {
                var parser = messageParser.get(qName);
                if (parser != null) {
                    become(parser, p -> {
                        partialMessage = p.result();
                    });
                }
            }
        }

        @Override
        public Try<CtiMessage> result() {
            return parsedMessage();
        }

        Try<CtiMessage> parsedMessage() {
            if (callID.isPresent()) {
                return partialMessage.flatMap(p -> {
                    return p.withCallID(callID.get());
                });
            }
            return Try.Failure(new NoSuchElementException("no message element found"));
        }
    }

    private class CallIdParser extends Unmarshaller<String> {
        @Override
        void text(String txt) {
            complete(Try.fromOptional(Optional.ofNullable(txt).map(String::trim)));
        }
    }

//    static private class LoginRespParser extends MessageElementHandler {
//
//        LoginRespParser(String qName, Attributes attributes) {
//            var IserverVer = Optional.ofNullable(attributes.getValue("IServerVer"))
//                    .map(String::trim);
//
//            var Result = Try.fromOptional(Optional.ofNullable(attributes.getValue("Result"))
//                    .map(String::trim));
//
//            var Status = Try.fromOptional(Optional.ofNullable(attributes.getValue("Status"))
//                    .map(String::trim));
//
//            partialMessage(callID -> Try.from(() -> new LoginResp( callID, IserverVer, Result.get() , Status.get() )));
//        }
//    }
//
//    static private class LoginReqParser extends MessageElementHandler {
//        @SuppressWarnings("OptionalGetWithoutIsPresent")
//        public LoginReqParser(String qName, Attributes attributes) {
//            var clientName = Optional.ofNullable(attributes.getValue("ClientName"));
//
//            partialMessage(callID -> Try.from(() -> new LoginReq( callID, clientName.get())));
//        }
//    }


}
