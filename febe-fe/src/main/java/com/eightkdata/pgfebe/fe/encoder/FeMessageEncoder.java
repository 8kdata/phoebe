/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.encoder;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.encoder.MessageEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.annotation.concurrent.Immutable;

/**
 * Created: 29/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class FeMessageEncoder extends MessageToByteEncoder<FeBeMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, FeBeMessage msg, ByteBuf out) throws Exception {
        FeBeMessageType type = msg.getType();
        int payloadSize = msg.computePayloadLength();
        int totalSize = type.getHeaderLength() + payloadSize;
        int feMessageSize = (null == type.getType() ? totalSize : totalSize - 1);

        // Reserve buffer size
        out.capacity(totalSize);

        // Header
        if(null != type.getType()) {
            out.writeByte(type.getType());
        }
        out.writeInt(feMessageSize);
        if(null != type.getSubtype()) {
            out.writeInt(type.getSubtype());
        }

        // Payload
        MessageEncoder encoder = FeMessageTypeEncoder.valueOf(type.name()).getEncoder();
        encoder.encode(msg, out.nioBuffer(out.writerIndex(), payloadSize));

        // Advance ByteBuf's writeIndex
        out.writerIndex(totalSize);
    }
}
