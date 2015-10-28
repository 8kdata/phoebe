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


package com.eightkdata.phoebe.common.messages;

import com.eightkdata.phoebe.common.message.AbstractByteBufMessage;
import com.eightkdata.phoebe.common.message.MessageType;
import com.eightkdata.phoebe.common.util.ByteBufAllocatorUtil;
import com.google.common.base.MoreObjects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public final class ReadyForQuery extends AbstractByteBufMessage {

    public enum StatusIndicator {
        /**
         * Idle (not in a transaction block)
         */
        IDLE('I'),
        /**
         * In a transaction block
         */
        TRANSACTION_BLOCK('T'),
        /**
         * In a failed transaction block (queries will be rejected until block is ended)
         */
        FAILED_TRANSACTION_BLOCK('E');

        private final byte statusByte;

        StatusIndicator(char statusChar) {
            this.statusByte = (byte) statusChar;
        }

        public byte getStatusByte() {
            return statusByte;
        }

        public static StatusIndicator getByByte(byte b) {
            // A more elegant implementation would be a static Map with every instance mapping.
            // But we avoid that for simplicity in this case, avoid Map's overhead and avoid byte boxing/unboxing

            switch (b) {
                case 'I':   return IDLE;
                case 'T':   return TRANSACTION_BLOCK;
                case 'E':   return FAILED_TRANSACTION_BLOCK;
                default:    throw new IllegalArgumentException("Uknown StatusIndicator with byte '" + b + "'");
            }
        }
    }

    public ReadyForQuery(@Nonnull ByteBuf byteBuf) {
        super(byteBuf);
    }

    static ReadyForQuery encode(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnull StatusIndicator statusIndicator
    ) {
        checkNotNull(statusIndicator);

        ByteBuf byteBuf = ByteBufAllocatorUtil.allocNonStringByteBuf(
                byteBufAllocator, MessageType.ReadyForQuery.getFixedMessageLength()
        );
        byteBuf.writeByte(statusIndicator.statusByte);

        return new ReadyForQuery(byteBuf);
    }

    @Override @Nonnull
    public MessageType getType() {
        return MessageType.ReadyForQuery;
    }

    public StatusIndicator getStatusIndicator() {
        // TODO: handle IllegalArgumentException.
        // Probably getByByte() should rather return null and throw a MessageDecodingException or similar

        return StatusIndicator.getByByte(byteBuf.readByte());
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add("status_indicator", getStatusIndicator());
    }

}
