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

import com.eightkdata.phoebe.common.FeBeMessage;
import com.eightkdata.phoebe.common.FeBeMessageType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public final class AuthenticationMD5Password implements FeBeMessage {
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

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.AuthenticationMD5Password;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        return 8; // 4 byte subtype code and 4 bytes of salt
    }

    @Override
    public String toString() {
        return getType().name() + "(salt=0x"
                + Integer.toHexString(salt[0])
                + Integer.toHexString(salt[1])
                + Integer.toHexString(salt[2])
                + Integer.toHexString(salt[3]) + ")";
    }

}
