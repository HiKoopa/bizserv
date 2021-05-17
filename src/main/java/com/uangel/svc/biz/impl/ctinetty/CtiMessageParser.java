package com.uangel.svc.biz.impl.ctinetty;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class CtiMessageParser {
    SAXParserFactory factory = SAXParserFactory.newInstance();


    SAXParser parser;

    public CtiMessageParser() throws SAXException, ParserConfigurationException {
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);

        parser = factory.newSAXParser();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public CtiMessage parse(byte[] b ) throws IOException, SAXException {
        var bis = new ByteArrayInputStream(b);
        var handler = new CtiXmlHandler();
        parser.parse( bis , handler);
        return handler.getParsed().get();
    }
}
