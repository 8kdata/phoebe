/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.BaseFeBeMessage;
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
public class AuthenticationMD5Password extends BaseFeBeMessage {
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
