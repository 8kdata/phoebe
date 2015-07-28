/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;

/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class FeBeMessageParsedHeaderBufferPayload {
    private final FeBeMessageType febeMessageType;
    private final int headerLength;
    private final long messageLength;
    private final ByteBuffer payload;

    /**
     * A container for the results of parsing a message header
     * @param febeMessageType the type of the febe message
     * @param messageLength the message length, including the byte type (if present) and the length field
     * @param headerLength the length of the header (type, length, subtype)
     * @param payload the bytes representing the payload of the message.
     *                Null if the message has no payload (only header).
     */
    public FeBeMessageParsedHeaderBufferPayload(
            @Nonnull FeBeMessageType febeMessageType, int headerLength, long messageLength,
            @Nullable ByteBuffer payload
    ) {
        Preconditions.checkNotNull(febeMessageType);
        Preconditions.checkArgument(headerLength > 0);
        Preconditions.checkArgument(
                messageLength > headerLength && payload != null
                        && payload.remaining() == (messageLength - headerLength)
                ||
                messageLength == headerLength && payload == null
        );

        this.febeMessageType = febeMessageType;
        this.messageLength = messageLength;
        this.headerLength = headerLength;
        this.payload = payload;
    }

    public FeBeMessageType getFeBeMessageType() {
        return febeMessageType;
    }

    public long getMessageLength() {
        return messageLength;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public ByteBuffer getPayload() {
        return payload;
    }

    public boolean hasPayload() {
        return payload != null;
    }
}
