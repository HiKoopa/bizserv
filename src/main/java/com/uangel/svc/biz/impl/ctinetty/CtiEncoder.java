package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.impl.ctimessage.CtiMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CtiEncoder extends MessageToByteEncoder<CtiMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CtiMessage ctiMessage, ByteBuf byteBuf)  {
        var a = ctiMessage.toXML();
        if (a.isSuccess()) {
            //Header
            byteBuf.writeByte(0);
            byteBuf.writeByte(GLIMsgType.GLI_MSG_TYPE_XML_DATA.getValue());
            byteBuf.writeShort(a.get().length + 2);
            byteBuf.writeByte(GLIHeader.GLI_VERSION);
            byteBuf.writeByte(GLIHeader.GLI_DEFAULT_APP);

            //Body
            byteBuf.writeBytes(a.get());

            log.info("write message {}", new String(a.get()));
        }
    }
}
