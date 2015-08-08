/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.phoebe.client.encoder;

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.MessageType;
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
public class FeMessageEncoder extends MessageToByteEncoder<Message> {
    private final Charset encoding;

    public FeMessageEncoder(Charset encoding) {
        this.encoding = encoding;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        int payloadSize = message.computePayloadLength(encoding);
        MessageType messageType = message.getType();
        int totalSize = messageType.headerLength() + payloadSize;
        int feMessageSize = messageType.hasType() ? totalSize - 1 : totalSize;

        // reserve extra space if necessary
        if (totalSize > out.capacity()) {
            out.capacity(totalSize);
        }

        // Header
        if (messageType.hasType()) {
            out.writeByte(messageType.getType());
        }
        out.writeInt(feMessageSize);
        if(messageType.hasSubType()) {
            out.writeInt(messageType.getSubtype());
        }

        // Payload
        Message.Encoder encoder = FeMessageTypeEncoder.valueOf(messageType.name()).getEncoder();
        if(null == encoder) {
            throw new UnsupportedOperationException(messageType + " Encoder");
        }

        encoder.encode(message, out, encoding);
    }
}
