package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.functional.Try;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.function.Consumer;

@Slf4j
class UnmarshallerStack extends DefaultHandler  {

    public UnmarshallerStack(Unmarshaller<?> rootUnmarshaller) {
        become(rootUnmarshaller, unmarshaller -> {});
    }

    static class HandlerStack {
        Unmarshaller<?> handler;
        Runnable whenPop;

        public HandlerStack(Unmarshaller<?> handler, Runnable whenPop) {
            this.handler = handler;
            this.whenPop = whenPop;
        }
    }

    LinkedList<HandlerStack> handler = new LinkedList<>();

    Attributes elemAttributes;

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        //log.info("start element {}", qName);
        elemAttributes = attributes;
        handler.getFirst().handler.elem(qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //log.info("end element {}", qName);
        var h = handler.removeFirst();
        h.whenPop.run();
    }

    @Override
    public void endDocument() {
        //log.info("end document ");

        var h = handler.removeFirst();
        h.whenPop.run();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        handler.getFirst().handler.text(new String(ch, start, length));
    }

    <T> void become(Unmarshaller<T> h , Consumer<Unmarshaller<T>> consumer) {
        //log.info("become {}", h.getClass());
        h.setStack(this);
        handler.addFirst(new HandlerStack( h, () -> {
            consumer.accept(h);
            elemAttributes = null;
        }));

        if (elemAttributes != null) {
            h.attr(elemAttributes);
        }
    }
}

class Unmarshaller<T> {
    UnmarshallerStack stack;

    void setStack(UnmarshallerStack stack) {
        this.stack = stack;
    }

    Try<T> ret;

    Try<T> result() {
        if (ret == null) {
            return Try.Failure(new Exception("result not exists"));
        }
        return ret;
    }

    void success( T t ) {
        ret = Try.Success(t);
    }

    void complete( Try<T> t ) {
        ret = t;
    }

    void attr( Attributes attr) {

    }

    void elem(String qName, Attributes attr) {

    }

    void text(String txt) {

    }

    <U> void become(Unmarshaller<U> u , Consumer<Unmarshaller<U>> c) {
        this.stack.become(u, c);
    }
}

interface PartialMessage {
    Try<CtiMessage> withCallID( String callID );
}

class MessageUnmarshaller extends Unmarshaller<PartialMessage> {

    void parseCallID( PartialMessage m ) {
        success(m);
    }
}
