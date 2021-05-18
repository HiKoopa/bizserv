package com.uangel.svc.biz.cti;

import com.uangel.svc.biz.actorutil.Try;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.Consumer;

public class GctiMsgWriter {

    static XMLOutputFactory factory = XMLOutputFactory.newFactory();


    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    Try<XMLStreamWriter> writer;

    public GctiMsgWriter() {
        writer = Try.from(() -> {
            var w = factory.createXMLStreamWriter(bos, "ISO-8859-1");
            w.writeStartDocument("ISO-8859-1", "1.0");
            w.writeDTD("<!DOCTYPE GctiMsg SYSTEM 'IServer.dtd'>");
            w.writeStartElement("GctiMsg");
            return w;
        });
    }

    @SuppressWarnings("CodeBlock2Expr")
    public Try<byte[]> toBytes() {
        return writer.flatMap(xmlStreamWriter -> {
            return Try.from(() -> {
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
                xmlStreamWriter.flush();
                return bos.toByteArray();
            });
        });
    }

    @SuppressWarnings("CodeBlock2Expr")
    public void writeElement(String name , Consumer<GctiMsgWriter> cf) {
        writer = writer.flatMap(xmlStreamWriter -> {
            return Try.from(() -> {
                xmlStreamWriter.writeStartElement(name);
                cf.accept(this);
                xmlStreamWriter.writeEndElement();
                return xmlStreamWriter;
            });
        });
    }

    @SuppressWarnings("CodeBlock2Expr")
    public void writeText(String text) {
        writer = writer.flatMap(xmlStreamWriter -> {
            return Try.from(() -> {
                xmlStreamWriter.writeCharacters(text);
                return xmlStreamWriter;
            });
        });
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CodeBlock2Expr"})
    public void writeAttribute(String attrName, Optional<String> value) {
        writer = writer.flatMap(xmlStreamWriter -> {
            return Try.from(() -> {

                if (value.isPresent()){
                    xmlStreamWriter.writeAttribute(attrName, value.get());
                }
                return xmlStreamWriter;
            });
        });
    }

    @SuppressWarnings("CodeBlock2Expr")
    public void writeAttribute(String attrName, String value) {
        writer = writer.flatMap(xmlStreamWriter -> {
            return Try.from(() -> {
                if (value != null){
                    xmlStreamWriter.writeAttribute(attrName, value);
                }
                return xmlStreamWriter;
            });
        });
    }
}
