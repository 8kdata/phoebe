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

import com.eightkdata.phoebe.common.BaseFixedLengthMessage;
import com.eightkdata.phoebe.common.MessageType;
import com.google.common.base.MoreObjects;
import com.google.common.io.BaseEncoding;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public final class AuthenticationMD5Password extends BaseFixedLengthMessage {
    public static final int SALT_LENGTH = 4;

    private final byte[] salt;

    public AuthenticationMD5Password(@Nonnull byte[] salt) {
        this.salt = checkNotNull(salt, "salt");
        checkArgument(salt.length == SALT_LENGTH, "salt must be %s bytes, found %d", SALT_LENGTH, salt.length);
    }

    /**
     * Get the salt to be used when hashing the password.
     *
     * The array returned by this method is a copy of the actual salt.
     * @return the salt
     */
    public @Nonnull byte[] getSalt() {
        return salt.clone();
    }

    /**
     * Encode a suitable response based on this message.
     *
     * @param username the username to respond with
     * @param password the password to respond with
     * @return the response string, suitable for using with a {@link PasswordMessage}.
     */
    public String response(String username, String password, Charset encoding) {
        return MD5.encode(username, password, salt, encoding);
    }

    @Override
    public MessageType getType() {
        return MessageType.AuthenticationMD5Password;
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add(
                "salt",
                "0x" + BaseEncoding.base16().lowerCase().encode(salt)
        );
    }
}
