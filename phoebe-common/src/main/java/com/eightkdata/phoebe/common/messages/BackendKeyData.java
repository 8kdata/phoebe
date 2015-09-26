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
import com.eightkdata.phoebe.common.util.ByteSize;
import com.google.common.base.MoreObjects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public final class BackendKeyData extends AbstractByteBufMessage {

    public BackendKeyData(@Nonnull ByteBuf byteBuf) {
        super(byteBuf);
    }

    static BackendKeyData encode(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnegative int pid, @Nonnegative int secret
    ) {
        checkArgument(pid > 0, "Pid must be a positive number (was: " + pid + ")");
        checkArgument(secret >= 0, "Secret must be a non-negative number (was: " + secret + ")");

        ByteBuf byteBuf = allocateByteBuf(byteBufAllocator, MessageType.BackendKeyData.getFixedMessageLength());
        byteBuf.writeInt(pid);
        byteBuf.writeInt(secret);

        return new BackendKeyData(byteBuf);
    }

    @Override
    public MessageType getType() {
        return MessageType.BackendKeyData;
    }

    public int getPid() {
        return byteBuf.readInt();
    }

    public int getSecret() {
        return byteBuf.slice(ByteSize.INTEGER, ByteSize.INTEGER).readInt();
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add("pid", getPid());
        toStringHelper.add("secret", getSecret());
    }

}
