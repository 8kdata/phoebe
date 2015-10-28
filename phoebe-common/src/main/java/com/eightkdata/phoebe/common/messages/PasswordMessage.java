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

import com.eightkdata.phoebe.common.message.AbstractCharsetByteBufMessage;
import com.eightkdata.phoebe.common.message.MessageType;
import com.eightkdata.phoebe.common.util.ByteBufAllocatorUtil;
import com.eightkdata.phoebe.common.util.ByteSize;
import com.eightkdata.phoebe.common.util.DecodingUtil;
import com.eightkdata.phoebe.common.util.EncodingUtil;
import com.google.common.base.MoreObjects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public final class PasswordMessage extends AbstractCharsetByteBufMessage {

    public PasswordMessage(@Nonnull ByteBuf byteBuf, @Nonnull Charset charset) {
        super(byteBuf, charset);
    }

    static PasswordMessage encode(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnull Charset charset, @Nonnull CharSequence password
    ) {
        checkNotNull(password);
        ByteBuf byteBuf = ByteBufAllocatorUtil.allocStringByteBuf(
                byteBufAllocator, EncodingUtil.lengthCString(password.toString(), charset)
        );
        EncodingUtil.writeCString(byteBuf, password.toString(), charset);

        return new PasswordMessage(byteBuf, charset);
    }

    @Override @Nonnull
    public MessageType getType() {
        return MessageType.PasswordMessage;
    }

    public CharSequence getPassword() {
        return DecodingUtil.getCString(byteBuf, 0, charset);
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add("password", "********");
    }

    @Override
    public int size() {
        // Oddly enough, this message supports both text and binary data (currently only text is supported).
        // Due to this support, the reported length should be of the real content,
        // excluding the null terminator byte used to mark the end of text data.

        return super.size() - ByteSize.BYTE;
    }
}
