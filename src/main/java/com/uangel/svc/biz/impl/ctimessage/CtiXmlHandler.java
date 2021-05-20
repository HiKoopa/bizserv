package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.SupplierEx;
import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CtiXmlHandler extends DefaultHandler {

    private static Throwable noMessage = new NoSuchElementException("");
    static class HandlerStack {
        ElementHandler<?> handler;
        Runnable whenPop;

        public HandlerStack(ElementHandler<?> handler, Runnable whenPop) {
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


    private <T extends ElementHandler<?>> void become(T h , Consumer<T> consumer) {
        h.setHandler(handler);
        handler.addFirst(new HandlerStack( h, () -> consumer.accept(h)));
    }

    static abstract class ElementHandler<T> extends DefaultHandler {


        private LinkedList<HandlerStack> handler;

        void setHandler(LinkedList<HandlerStack> handler) {
            this.handler = handler;
        }

        <U extends ElementHandler<?>> void become(U h , Consumer<U> consumer) {
            h.setHandler(handler);
            handler.addFirst(new HandlerStack( h, () -> consumer.accept(h)));
        }

        Try<T> res = Try.Failure(noMessage);

        public Try<T> result() {
            return res;
        }

        public void result( T r) {
            res = Try.Success(r);
        }

        public void supply(SupplierEx<T> f)   {
            res = Try.from(f);
        }

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

    static class MessageElementHandler extends ElementHandler<PartialMessage> {
        void partialMessage( PartialMessage m ) {
            result(m);
        }
    }

    @SuppressWarnings("CodeBlock2Expr")
    private static class CallStatusParser extends MessageElementHandler {
        public CallStatusParser(String qName, Attributes attributes) {
            partialMessage(callID -> {
                return Try.fromOptional(Optional.ofNullable(attributes.getValue("Event"))).map(e -> {
                    return new CallStatus(callID, e);
                });
            });
        }
    }

    private class RootParser extends ElementHandler<CtiMessage> {

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

        @Override
        public Try<CtiMessage> result() {
            return ret;
        }
    }

    interface PartialMessage {
        Try<CtiMessage> withCallID( String callID );

    }

    static Map<String, BiFunction< String, Attributes, MessageElementHandler>> messageParser = new HashMap<>();

    static {
        messageParser.put("LoginResp", LoginRespParser::new);

        messageParser.put("LoginReq", LoginReqParser::new);

        messageParser.put("CallStatus", CallStatusParser::new);
        messageParser.put("NewCall", NewCallParser::new);

    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CodeBlock2Expr"})
    private class GctiParser extends ElementHandler<CtiMessage> {
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
                    become(parser.apply(qName, attributes), p -> {
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

    @SuppressWarnings("CodeBlock2Expr")
    private class CallIdParser extends ElementHandler<String> {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        @Override
        void text(String txt) {
            supply(() -> {
                return Optional.ofNullable(txt).map(String::trim).get();
            });
        }
    }

    static private class LoginRespParser extends MessageElementHandler {

        LoginRespParser(String qName, Attributes attributes) {
            var IserverVer = Optional.ofNullable(attributes.getValue("IServerVer"))
                    .map(String::trim);

            var Result = Try.fromOptional(Optional.ofNullable(attributes.getValue("Result"))
                    .map(String::trim));

            var Status = Try.fromOptional(Optional.ofNullable(attributes.getValue("Status"))
                    .map(String::trim));

            partialMessage(callID -> Try.from(() -> new LoginResp( callID, IserverVer, Result.get() , Status.get() )));
        }
    }

    static private class LoginReqParser extends MessageElementHandler {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public LoginReqParser(String qName, Attributes attributes) {
            var clientName = Optional.ofNullable(attributes.getValue("ClientName"));

            partialMessage(callID -> Try.from(() -> new LoginReq( callID, clientName.get())));
        }
    }

    static private class NewCallParser extends MessageElementHandler {
        public NewCallParser(String qName, Attributes attributes) {
        }

        @SuppressWarnings("CodeBlock2Expr")
        @Override
        void elem(String qName, Attributes attributes) {
            if ("CalledNum".equals(qName)) {
                become(new CalledNumParser(), elementHandler -> {
                    partialMessage(callID -> elementHandler.result().map(called -> {
                        return new NewCall(callID, called);
                    }));
                });
            }
        }
    }

    static private class CalledNumParser extends ElementHandler<String>{
        @Override
        void text(String txt) {
            result(txt.trim());
        }
    }
}
