package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CtiMessageParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class CtiDecoder extends ByteToMessageDecoder {
    CtiMessageParser parser = new CtiMessageParser();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 충분한 크기만큼 read 되지 않았을 때,  원래대로 돌리기 위해 index 저장
        var index = byteBuf.readerIndex();

        // header의 size가 16 byte 여서
        // header가 완전히 read 되었는지 테스트
        if (byteBuf.readableBytes() >= 6) {

            byteBuf.readByte();
            var msgType = byteBuf.readByte();

            //Header 포함 길이
            var length = byteBuf.readShort();
            byteBuf.readByte();
            byteBuf.readByte();
            //dump로 분석결과 body length는 위 length에서 -2해야함
            //헤더에 기록된 length바이트배열의 뒤가 header에 기록된 length
            byte[] b = null;
            if (byteBuf.readableBytes() >= length-2) {
                if (length-2 > 0) {
                    b = new byte[length-2];
                    byteBuf.readBytes(b);
                }
                switch (GLIMsgType.valueOf(msgType).get()) {
                    case GLI_MSG_TYPE_KEEP_ALIVE_REQ:
                    case GLI_MSG_TYPE_KEEP_ALIVE_ACK:
                    case GLI_MSG_TYPE_ERROR_ACK:
                        //list.add();
                        break;
                    case GLI_MSG_TYPE_XML_DATA:
                        //SAX Parser
                        if (b != null) {
                            var parsed = parser.parse(b);
                            if (parsed.isSuccess()) {
                                list.add(parsed.get());
                            } else {
                                ctx.fireExceptionCaught(parsed.failed().get());
                            }
                        } else {
                            ctx.fireExceptionCaught(new Exception());
                        }
                        break;
                    default:
                        break;
                }
                return;
            }
        }

        // 충분한 길이만큼 read가 안되었으면  , index 를 원래대로 돌림
        byteBuf.readerIndex(index);
    }

}
