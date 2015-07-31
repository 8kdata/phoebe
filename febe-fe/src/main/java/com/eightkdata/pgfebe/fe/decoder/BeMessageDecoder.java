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

import com.eightkdata.pgfebe.common.BeMessageType;
import com.eightkdata.pgfebe.common.FeBe;
import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.decoder.MessageDecoder;
import com.eightkdata.pgfebe.common.exception.InvalidMessageException;
import com.google.common.primitives.Ints;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * This pipeline stage is responsible for decoding the raw byte stream into messages.
 *
 * The actual processing is handled by {@link BeMessageProcessor}.
 */
public class BeMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < BeMessageType.MIN_HEADER_SIZE) {
            return;
        }

        // Decode type and length
        byte type = in.readByte();
        long length = in.readUnsignedInt();
        if(length > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Messages with size exceeding 2GB are not supported");
        }
        int headerLength = BeMessageType.MIN_HEADER_SIZE;

        // Find exact type, checking subtype, if needed (only for auth messages)
        BeMessageType beMessageType;
        if(type == FeBe.AUTH_MESSAGE_TYPE) {
            if(in.readableBytes() < Ints.BYTES) {
                return;
            }
            int subtype = in.readInt();
            headerLength += Ints.BYTES;
            beMessageType = BeMessageType.getAuthMessageBySubtype(subtype);
        } else {
            beMessageType = BeMessageType.getNonAuthMessageByType(type);
        }

        // Check for invalid messages
        if (beMessageType == null) {
            throw new InvalidMessageException("unknown message type (" + type + ")");
        }
        FeBeMessageType messageType = beMessageType.getFeBeMessageType();
        if (messageType.hasFixedLength() && length != messageType.getLength()) {
            throw new InvalidMessageException(
                    "unexpected length (" + length + ") for " + messageType + "(" + (char) type + ") message.");
        }

        // Extract payload, if any
        int payloadLength = 1 + (int) length - headerLength;
        if(payloadLength == 0) {
            out.add(messageType.getHeaderOnlyInstance());
            return;
        }

        assert messageType.hasPayload();

        FeBeMessage febeMessage;
        MessageDecoder<?> decoder = BeMessageTypeDecoder.valueOf(beMessageType.name()).getDecoder();
        febeMessage = decoder.decode(in.slice(in.readerIndex(), payloadLength).nioBuffer());

        out.add(febeMessage);

        // Mark all payload bytes as read, so that this decoder will only be called again if there's more input
        in.readerIndex(in.readerIndex() + payloadLength);
    }

}
