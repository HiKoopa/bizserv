package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;

public class CtiMessageParser {
    SAXParserFactory factory = SAXParserFactory.newInstance();


    Try<SAXParser> parser;

    public CtiMessageParser() {
        parser = Try.from(() -> {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);

            return factory.newSAXParser();
        });
    }

    @SuppressWarnings("CodeBlock2Expr")
    public Try<CtiMessage> parse(byte[] b )  {
        return parser.flatMap(saxParser -> {
            return Try.from(() -> {
                var bis = new ByteArrayInputStream(b);

                var stack = new UnmarshallerStack();

                var handler = new CtiMessageUnmarshaller(stack);


                saxParser.parse( bis , stack);


                return handler.result();
            }).flatMap((t) -> t).recoverWith(throwable -> {
                throwable.printStackTrace();
                return Try.Failure(new Exception( String.format("parse error. message = %s", new String(b)) , throwable));
            });
        });
    }
}
