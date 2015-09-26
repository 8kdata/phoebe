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
import com.eightkdata.phoebe.common.util.MD5;
import com.google.common.base.MoreObjects;
import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public final class AuthenticationMD5Password extends AbstractByteBufMessage {
    public static final int SALT_LENGTH = 4;

    public AuthenticationMD5Password(@Nonnull ByteBuf byteBuf) {
        super(byteBuf);
    }

    static AuthenticationMD5Password encode(@Nonnull ByteBufAllocator byteBufAllocator, @Nonnull byte[] salt) {
        checkNotNull(salt);
        checkArgument(SALT_LENGTH == salt.length, "salt must be %s bytes, found %d", SALT_LENGTH, salt.length);

        ByteBuf byteBuf = allocateByteBuf(byteBufAllocator, SALT_LENGTH);
        byteBuf.writeBytes(salt);

        return new AuthenticationMD5Password(byteBuf);
    }

    /**
     * Get the salt to be used when hashing the password.
     *
     * The array returned by this method is a copy of the actual salt.
     * @return the salt
     */
    public @Nonnull byte[] getSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        byteBuf.getBytes(0, salt);

        return salt;
    }

    /**
     * Encode a suitable response based on this message.
     *
     * @param username the username to respond with
     * @param password the password to respond with
     * @return the response string, suitable for using with a {@link PasswordMessage}.
     */
    public String response(String username, String password, Charset encoding) {
        return MD5.encode(username, password, getSalt(), encoding);
    }

    @Override
    public MessageType getType() {
        return MessageType.AuthenticationMD5Password;
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add(
                "salt",
                "0x" + BaseEncoding.base16().lowerCase().encode(getSalt())
        );
    }
}
