package com.uangel.svc.biz.cti;

import com.uangel.svc.biz.actorutil.SupplierEx;
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
import java.util.function.Supplier;

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
        handler.addFirst(new HandlerStack( h, () -> consumer.accept(h)));
    }

    static abstract class ElementHandler<T> extends DefaultHandler {

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

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CodeBlock2Expr"})
    private class GctiParser extends ElementHandler<CtiMessage> {
        Optional<String> callID = Optional.empty();
        Optional<LoginResp> loginResp = Optional.empty();
        Optional<String> loginReq = Optional.empty();
        Optional<NewCall> newCall = Optional.empty();

        @Override
        public void elem(String qName, Attributes attributes)  {
            switch (qName) {
                case "CallId":
                    become(new CallIdParser(), callIdParser -> {
                        callID = callIdParser.result().toOptional();
                    });
                    break;
                case "LoginResp":
                    become(new LoginRespParser(qName, attributes), loginRespParser -> {
                        loginResp = loginRespParser.result().toOptional();
                    });
                    break;
                case "LoginReq":
                    become(new LoginReqParser(qName, attributes), loginReqParser -> {
                        loginReq = loginReqParser.result().toOptional();
                    });
                    break;
                case "NewCall":
                    become(new NewCallParser(qName, attributes), newCallParser -> {
                        newCall = newCallParser.result().toOptional();
                    });
                    break;
            }
        }

        @Override
        public Try<CtiMessage> result() {
            return parsedMessage();
        }

        Try<CtiMessage> parsedMessage() {
            if (callID.isPresent()) {
                if (loginResp.isPresent()) {
                    return Try.fromOptional(loginResp).map(v -> {
                        return new LoginResp(callID.get(), v.getIServerVer(), v.getResult(), v.getStatus());
                    });
                }
                if (loginReq.isPresent()) {
                    return Try.fromOptional(loginReq).map(v -> {
                            return new LoginReq(
                                    this.callID.get(),
                                    v);
                    });
                }
                if (newCall.isPresent()) {
                    return Try.fromOptional(newCall).map(v -> {
                        return new NewCall(
                                this.callID.get(),
                                v.getCalledNum());
                    });
                }
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

    private class LoginRespParser extends ElementHandler<LoginResp> {



        LoginRespParser(String qName, Attributes attributes) {
            supply(() -> {
                var IserverVer = Optional.ofNullable(attributes.getValue("IServerVer"))
                        .map(String::trim);

                var Result = Try.fromOptional(Optional.ofNullable(attributes.getValue("Result"))
                        .map(String::trim));

                var Status = Try.fromOptional(Optional.ofNullable(attributes.getValue("Status"))
                        .map(String::trim));

                return new LoginResp( "", IserverVer, Result.get() , Status.get() );
            });
        }
    }

    private class LoginReqParser extends ElementHandler<String> {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        public LoginReqParser(String qName, Attributes attributes) {
            supply(() -> {
                var clientName = Optional.ofNullable(attributes.getValue("ClientName"));
                return clientName.get();
            });
        }
    }

    private class NewCallParser extends ElementHandler<NewCall> {
        public NewCallParser(String qName, Attributes attributes) {
        }

        @SuppressWarnings("CodeBlock2Expr")
        @Override
        void elem(String qName, Attributes attributes) {
            if ("CalledNum".equals(qName)) {
                become(new CalledNumParser(), elementHandler -> {
                    supply(() -> new NewCall("", elementHandler.result().get()));
                });
            }
        }
    }

    private class CalledNumParser extends ElementHandler<String>{
        @Override
        void text(String txt) {
            result(txt.trim());
        }
    }
}
