package com.uangel.svc.biz.impl.ctinetty;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.text.Element;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CtiXmlHandler extends DefaultHandler {

    static class HandlerStack {
        ElementHandler handler;
        Runnable whenPop;

        public HandlerStack(ElementHandler handler, Runnable whenPop) {
            this.handler = handler;
            this.whenPop = whenPop;
        }
    }

    LinkedList<HandlerStack> handler = new LinkedList<>();

    Optional<CtiMessage> ret = Optional.empty();
    public CtiXmlHandler() {
        become(new RootParser(), rootParser -> {
            ret = rootParser.ret;
        });
    }

    public Optional<CtiMessage> getParsed() {
        if (handler.size() > 0 ) {
            handler.removeFirst().whenPop.run();
        }
        return ret;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        handler.getFirst().handler.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        var h = handler.removeFirst();
        h.whenPop.run();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        handler.getFirst().handler.characters(ch,start,length);
    }


    private <T extends ElementHandler> void become(T h , Consumer<T> consumer) {
        handler.addFirst(new HandlerStack( h, () -> {
            consumer.accept(h);
        }));
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
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private class RootParser extends ElementHandler {

        Optional<CtiMessage> ret = Optional.empty();

        @Override
        public void elem(String qName, Attributes attributes)  {
            if (qName.equals("GctiMsg")) {
                become(new GctiParser(), gctiParser -> {
                    ret = gctiParser.parsedMessage();
                });
            }
        }
    }

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

        Optional<CtiMessage> parsedMessage() {
            if (callID.isPresent()) {
                if (loginResp.isPresent()) {
                    return this.loginResp.map(loginRespParser -> {
                        return new LoginResp(this.callID.get(), loginRespParser.IserverVer, loginRespParser.Result, loginRespParser.Status);
                    });
                }
            }
            return Optional.empty();
        }
    }


    private class CallIdParser extends ElementHandler {
        Optional<String> callID = Optional.empty();
        @Override
        void text(String txt) {
            this.callID = Optional.ofNullable(txt).map(s -> s.trim());
        }
    }

    private class LoginRespParser extends ElementHandler {
        Optional<String> IserverVer = Optional.empty();
        Optional<String> Result = Optional.empty();
        Optional<String> Status = Optional.empty();

        LoginRespParser(String qName, Attributes attributes) {
            this.IserverVer = Optional.ofNullable(attributes.getValue("IServerVer"))
                    .map(s -> s.trim());

            this.Result = Optional.ofNullable(attributes.getValue("Result"))
                    .map(s -> s.trim());

            this.Status = Optional.ofNullable(attributes.getValue("Status"))
                    .map(s -> s.trim());
        }
    }
}
