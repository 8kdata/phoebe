/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.phoebe.client.encoder;

import com.eightkdata.phoebe.common.FeBeMessage;
import com.eightkdata.phoebe.common.FeBeMessageType;
import com.eightkdata.phoebe.common.MessageEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

/**
 * Created: 29/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class FeMessageEncoder extends MessageToByteEncoder<FeBeMessage> {
    private final Charset encoding;

    public FeMessageEncoder(Charset encoding) {
        this.encoding = encoding;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FeBeMessage message, ByteBuf out) throws Exception {
        FeBeMessageType messageType = message.getType();
        int payloadSize = message.computePayloadLength(encoding);
        int totalSize = messageType.getHeaderLength() + payloadSize;
        int feMessageSize = (messageType.hasType() ? totalSize -1 : totalSize);

        // reserve extra space if necessary
        if (totalSize > out.capacity()) {
            out.capacity(totalSize);
        }

        // Header
        if (messageType.hasType()) {
            out.writeByte(messageType.getType());
        }
        out.writeInt(feMessageSize);
        if(null != messageType.getSubtype()) {
            out.writeInt(messageType.getSubtype());
        }

        // Payload
        MessageEncoder encoder = FeMessageTypeEncoder.valueOf(messageType.name()).getEncoder();
        if (encoder == null) {
            throw new UnsupportedOperationException(messageType + " Encoder");
        }

        encoder.encode(message, out, encoding);
    }
}
