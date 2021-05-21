package com.uangel.svc.biz.impl.ctimessage;

import com.uangel.svc.biz.actorutil.Try;
import org.xml.sax.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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

                var handler = new DocumentUnmarshaller();

                var stack = new UnmarshallerStack(handler);

                saxParser.parse( bis , stack);

                return handler.result();
            }).flatMap((t) -> t).recoverWith(throwable -> {
                throwable.printStackTrace();
                return Try.Failure(new Exception( String.format("parse error. message = %s", new String(b)) , throwable));
            });
        });
    }

    static Map<String, MessageUnmarshaller> messageParser = new HashMap<>();
    static void addUnmarshaller( String messageType , MessageUnmarshaller unmarshaller) {
        messageParser.put(messageType, unmarshaller);
    }

    static Optional<MessageUnmarshaller> getUnmarshaller(String messageType) {
        return Optional.ofNullable(messageParser.get(messageType));
    }

    static {
        messageParser.put("LoginResp", LoginResp.Unmarshaller());
        messageParser.put("LoginReq", LoginReq.Unmarshaller());
        messageParser.put("CallStatus", CallStatus.Unmarshaller());
        messageParser.put("NewCall", NewCall.Unmarshaller());
    }
}

class DocumentUnmarshaller extends Unmarshaller<CtiMessage> {

    private static Throwable noMessage = new NoSuchElementException("");

    Try<CtiMessage> ret = Try.Failure(noMessage);

    @SuppressWarnings("CodeBlock2Expr")
    public void elem(String qName, Attributes attributes)  {
        if (qName.equals("GctiMsg")) {
            become(new GctiParser(), gctiParser -> {
                complete(gctiParser.result());
            });
        }
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "CodeBlock2Expr"})
    static private class GctiParser extends Unmarshaller<CtiMessage> {
        Optional<String> callID = Optional.empty();
        Try<PartialMessage> partialMessage = Try.Failure(noMessage);

        @Override
        public void elem(String qName, Attributes attributes)  {
            if ("CallId".equals(qName)) {
                become(new CallIdParser(), callIdParser -> {
                    callID = callIdParser.result().toOptional();
                });
            } else {
                var parser = CtiMessageParser.getUnmarshaller(qName);
                if (parser.isPresent()) {
                    become(parser.get(), p -> {
                        partialMessage = p.result();
                    });
                } else {
                    become(unknownElement(qName) , p -> {
                        partialMessage = p.result();
                    });
                }
            }
        }

        private Unmarshaller<PartialMessage> unknownElement(String qName) {
            return new Unmarshaller<>() {
                @Override
                Try<PartialMessage> result() {
                    return Try.Failure(new Exception("unknown element " + qName));
                }
            };
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

    static private class CallIdParser extends Unmarshaller<String> {
        @Override
        void text(String txt) {
            complete(Try.fromOptional(Optional.ofNullable(txt).map(String::trim)));
        }
    }
}
