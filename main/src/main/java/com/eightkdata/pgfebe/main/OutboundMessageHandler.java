/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.main;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created: 26/06/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class OutboundMessageHandler extends MessageToByteEncoder<Object> {
    private final short POSTGRESQL_PROTOCOL_MAJOR = 3;
    private final short POSTGRESQL_PROTOCOL_MINOR = 0;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(POSTGRESQL_PROTOCOL_MAJOR);
        byteBuf.writeShort(POSTGRESQL_PROTOCOL_MINOR);

    // Validate possible startup params
        StringBuffer stringBuffer = new StringBuffer();
        writeParam(stringBuffer, "user", "aht");

        byteBuf.writeBytes(stringBuffer.toString().getBytes());

        byteBuf.writeByte(0);
    }

    private void writeParam(StringBuffer stringBuffer, String key, String value) {
        stringBuffer
                .append(key)
                .append("\0")
                .append(value)
                .append("\0")
        ;
    }
}
