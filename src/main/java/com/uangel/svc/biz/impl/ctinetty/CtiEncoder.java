package com.uangel.svc.biz.impl.ctinetty;

import com.uangel.svc.biz.cti.CtiMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

public class CtiEncoder extends MessageToByteEncoder<CtiMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CtiMessage ctiMessage, ByteBuf byteBuf) throws Exception {
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
        }
    }
}
