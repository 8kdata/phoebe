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


package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.BaseFixedLengthFeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class AuthenticationMD5Password extends BaseFixedLengthFeBeMessage {
    private final byte[] salt;

    private AuthenticationMD5Password(@Nonnull byte[] salt) {
        super(FeBeMessageType.AuthenticationMD5Password);
        this.salt = salt;
    }

    /**
     * Returns a copy of the salt. The original remains unmodified. Cache this value if required more than once
     * @return the salt
     */
    public @Nonnull byte[] getSalt() {
        return salt.clone();
    }

    @Nonnull
    @Override
    protected StringBuilder toStringMessagePayload(@Nonnull StringBuilder sb, @Nonnull String separator) {
        return sb.append(separator)
                .append("salt=0x")
                .append(BaseEncoding.base16().lowerCase().encode(salt));
    }

    public static class Builder implements MessageBuilder<AuthenticationMD5Password> {
        private byte[] salt;

        public Builder setSalt(@Nonnull byte[] salt) {
            Preconditions.checkNotNull(salt);
            Preconditions.checkArgument(salt.length == 4);

            this.salt = salt.clone();

            return this;
        }

        @Override
        public AuthenticationMD5Password build() {
            return new AuthenticationMD5Password(salt);
        }
    }
}
