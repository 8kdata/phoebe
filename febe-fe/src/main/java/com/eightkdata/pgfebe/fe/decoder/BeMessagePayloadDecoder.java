/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.decoder;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.MessageDecoder;
import com.eightkdata.pgfebe.common.message.HeaderOnlyMessage;
import com.eightkdata.pgfebe.fe.BeMessageParsedHeaderBufferPayload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class BeMessagePayloadDecoder extends MessageToMessageDecoder<BeMessageParsedHeaderBufferPayload> {
    @Override
    protected void decode(ChannelHandlerContext ctx, BeMessageParsedHeaderBufferPayload msg, List<Object> out)
    throws Exception {
        try {
            FeBeMessageType febeMessageType = msg.getFeBeMessageType();
            FeBeMessage febeMessage;
            if(msg.hasPayload()) {
                MessageDecoder<?> decoder = BeMessageTypeDecoder.valueOf(msg.getBeMessageType().name()).getDecoder();
                febeMessage = decoder.decode(msg.getPayload());
            } else {
                febeMessage = new HeaderOnlyMessage(febeMessageType);
            }

            out.add(febeMessage);
        } finally {
            if(msg.hasPayload())
                msg.releaseRetainedBuffer();
        }
    }
}
