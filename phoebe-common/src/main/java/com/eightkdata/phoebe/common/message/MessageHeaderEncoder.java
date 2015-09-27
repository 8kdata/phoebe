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


package com.eightkdata.phoebe.common.message;

import com.eightkdata.phoebe.common.util.ByteBufAllocatorUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import java.util.EnumMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class encapsulates the functionality to encode the header of a {@see Message} into a {@see ByteBuf}.
 * Some message types (those with a fixed length) have headers that are always the same.
 * This uses internally a thread-safe cache to cache those {@see ByteBuf}s.
 */
@ThreadSafe
public class MessageHeaderEncoder  {

    private final @Nonnull ByteBufAllocator byteBufAllocator;
    private final EnumMap<MessageType,ByteBuf> fixedHeadersCache = new EnumMap<MessageType, ByteBuf>(MessageType.class);

    public MessageHeaderEncoder(@Nonnull ByteBufAllocator byteBufAllocator) {
        this.byteBufAllocator = checkNotNull(byteBufAllocator);
    }

    /**
     * Encodes the message header for the given {@see MessageType} and message length.
     * If the message is of fixed-length, a cached version will be returned after the first invocation.
     */
    public @Nonnull ByteBuf encodeHeader(@Nonnull MessageType messageType, @Nonnegative int messageLength) {
        checkNotNull(messageType);
        checkArgument(messageLength > 0, "Message length must be a positive number");

            return ! messageType.isFixedLengthMessage() ?
                    doEncodeHeader(messageType, messageLength) :
                    getAndSetFixedHeaderCache(messageType);
    }

    private @Nonnull ByteBuf getAndSetFixedHeaderCache(@Nonnull MessageType messageType) {
        if(fixedHeadersCache.containsKey(messageType))
            return fixedHeadersCache.get(messageType);

        synchronized (this) {
            if(! fixedHeadersCache.containsKey(messageType)) {
                ByteBuf byteBuf = doEncodeHeader(messageType, messageType.getFixedTotalMessageLength());
                fixedHeadersCache.put(messageType, byteBuf);
            }
        }

        return fixedHeadersCache.get(messageType);
    }

    private @Nonnull ByteBuf doEncodeHeader(@Nonnull MessageType messageType, @Nonnegative int messageLength) {
        ByteBuf header = ByteBufAllocatorUtil.allocNonStringByteBuf(byteBufAllocator, messageType.headerLength());
        if (messageType.hasType()) {
            header.writeByte(messageType.getType());
        }
        header.writeInt(messageType.headerLength() + messageLength);
        if(messageType.hasSubType()) {
            header.writeInt(messageType.getSubtype());
        }

        return header;
    }

    /**
     * Release the {@see ByteBuf}s that might be retained in the fixed-length message cache
     * associated with this instance.
     */
    public void releaseByteBufs() {
        for(ByteBuf byteBuf : fixedHeadersCache.values()) {
            byteBuf.release();
        }
    }

}
