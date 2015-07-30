/*
 * Copyright (c) 2015, 8Kdata Technology S.L.
 *
 * Permission to use, copy, modify, and distribute this software and its documentation for any purpose,
 * without fee, and without a written agreement is hereby granted, provided that the above copyright notice and this
 * paragraph and the following two paragraphs appear in all copies.
 *
 * IN NO EVENT SHALL 8Kdata BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF 8Kdata HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 8Kdata SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS,
 * AND 8Kdata HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 */


package com.eightkdata.pgfebe.fe.decoder;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.decoder.MessageDecoder;
import com.eightkdata.pgfebe.common.message.HeaderOnlyMessage;
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
