package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.actorutil.Try;
import com.uangel.svc.biz.cti.CtiMessage;
import com.uangel.svc.biz.cti.LoginResp;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public class CtiXmlHandler extends DefaultHandler {

    private static Throwable noMessage = new NoSuchElementException("");
    static class HandlerStack {
        ElementHandler handler;
        Runnable whenPop;

        public HandlerStack(ElementHandler handler, Runnable whenPop) {
            this.handler = handler;
            this.whenPop = whenPop;
        }
    }

    LinkedList<HandlerStack> handler = new LinkedList<>();


    Try<CtiMessage> ret = Try.Failure(noMessage);
    @SuppressWarnings("CodeBlock2Expr")
    public CtiXmlHandler() {
        become(new RootParser(), rootParser -> {
            ret = rootParser.ret;
        });
    }

    public Try<CtiMessage> getParsed() {
        if (handler.size() > 0 ) {
            handler.removeFirst().whenPop.run();
        }
        return ret;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        handler.getFirst().handler.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        var h = handler.removeFirst();
        h.whenPop.run();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        handler.getFirst().handler.characters(ch,start,length);
    }


    private <T extends ElementHandler> void become(T h , Consumer<T> consumer) {
        handler.addFirst(new HandlerStack( h, () -> consumer.accept(h)));
    }

    static class ElementHandler extends DefaultHandler {


        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.elem(qName, attributes);
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            this.text(new String(ch, start, length));
        }

        void text(String txt) {

        }

        void elem(String qName, Attributes attributes) {

        }
    }
    private class RootParser extends ElementHandler {

        Try<CtiMessage> ret = Try.Failure(noMessage);

        @SuppressWarnings("CodeBlock2Expr")
        @Override
        public void elem(String qName, Attributes attributes)  {
            if (qName.equals("GctiMsg")) {
                become(new GctiParser(), gctiParser -> {
                    ret = gctiParser.parsedMessage();
                });
            }
        }
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CodeBlock2Expr"})
    private class GctiParser extends ElementHandler {
        Optional<String> callID = Optional.empty();
        Optional<LoginRespParser> loginResp = Optional.empty();

        @Override
        public void elem(String qName, Attributes attributes)  {
            switch (qName) {
                case "CallId":
                    become(new CallIdParser(), callIdParser -> {
                        callID = callIdParser.callID;
                    });
                    break;
                case "LoginResp":
                    become(new LoginRespParser(qName, attributes), loginRespParser -> {
                        loginResp = Optional.of(loginRespParser);
                    });
                    break;
            }
        }

        Try<CtiMessage> parsedMessage() {
            if (callID.isPresent()) {
                if (loginResp.isPresent()) {
                    return Try.fromOptional(loginResp).flatMap(loginRespParser -> {
                        return Try.from(() -> {
                            return new LoginResp(
                                    this.callID.get(),
                                    loginRespParser.IserverVer,
                                    loginRespParser.Result.get(),
                                    loginRespParser.Status.get()
                            );
                        });
                    });
                }
            }
            return Try.Failure(new NoSuchElementException("no message element found"));
        }
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private class CallIdParser extends ElementHandler {
        Optional<String> callID = Optional.empty();
        @Override
        void text(String txt) {
            this.callID = Optional.ofNullable(txt).map(String::trim);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private class LoginRespParser extends ElementHandler {

        Optional<String> IserverVer;
        Try<String> Result;
        Try<String> Status;

        LoginRespParser(String qName, Attributes attributes) {
            this.IserverVer = Optional.ofNullable(attributes.getValue("IServerVer"))
                    .map(String::trim);

            this.Result = Try.fromOptional(Optional.ofNullable(attributes.getValue("Result"))
                    .map(String::trim));

            this.Status = Try.fromOptional(Optional.ofNullable(attributes.getValue("Status"))
                    .map(String::trim));
        }
    }
}
