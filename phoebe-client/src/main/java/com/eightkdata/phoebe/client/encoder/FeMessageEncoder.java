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


package com.eightkdata.phoebe.client.encoder;

import com.eightkdata.phoebe.common.message.ByteBufMessage;
import com.eightkdata.phoebe.common.message.MessageHeaderEncoder;
import com.eightkdata.phoebe.common.message.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.annotation.concurrent.Immutable;

/**
 *
 */
@Immutable
public class FeMessageEncoder extends MessageToByteEncoder<ByteBufMessage> {

    private final MessageHeaderEncoder messageHeaderEncoder;

    public FeMessageEncoder(MessageHeaderEncoder messageHeaderEncoder) {
        this.messageHeaderEncoder = messageHeaderEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBufMessage message, ByteBuf out) throws Exception {
        assert out instanceof CompositeByteBuf : "out ByteBuf must be a composite ByteBuf";

        MessageType messageType = message.getType();

        // Write Header
        ByteBuf header = messageHeaderEncoder.encodeHeader(messageType, message.size());

        // Compose the header + body message
        CompositeByteBuf wholeMessage = (CompositeByteBuf) out;
        wholeMessage.addComponent(header);
        wholeMessage.addComponent(message.getByteBuf());
        wholeMessage.writerIndex(wholeMessage.capacity());
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBufMessage msg, boolean preferDirect)
    throws Exception {
        return new CompositeByteBuf(ctx.alloc(), true, 2);
    }

}
