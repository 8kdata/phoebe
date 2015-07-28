/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.decoder;

import com.eightkdata.pgfebe.common.BeMessageType;
import com.eightkdata.pgfebe.common.FeBe;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.exception.FeBeException;
import com.eightkdata.pgfebe.common.exception.FeBeExceptionType;
import com.eightkdata.pgfebe.common.netty.RetainedByteBuf;
import com.eightkdata.pgfebe.fe.BeMessageParsedHeaderBufferPayload;
import com.google.common.primitives.Ints;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created: 28/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class BeMessageHeaderDecoder extends ByteToMessageDecoder {
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
        if(beMessageType == null) {
            throw new FeBeException(FeBeExceptionType.INVALID_MESSAGE, "Unknown be message byte type (" + type + ")");
        }
        FeBeMessageType febeMessageType = beMessageType.getFeBeMessageType();
        if(febeMessageType.hasFixedLength() && length != febeMessageType.getLength()) {
            throw new FeBeException(
                    FeBeExceptionType.INVALID_MESSAGE,
                    "Unexpected length (" + length + ") for be message type=" + (char) type
            );
        }

        // Extract payload, if any
        int payloadLength = 1 + (int) length - headerLength;
        if(payloadLength == 0) {
            out.add(febeMessageType.getHeaderOnlyInstance());
        } else {
            out.add(new BeMessageParsedHeaderBufferPayload(
                    febeMessageType, beMessageType, headerLength,
                    1 + length,     // decoded length does not include byte type
                    new RetainedByteBuf(in.slice(in.readerIndex(), payloadLength))
            ));

            // Mark all payload bytes as read, so that this decoder will only be called again if there's more input
            in.readerIndex(in.readerIndex() + payloadLength);
        }
    }
}
