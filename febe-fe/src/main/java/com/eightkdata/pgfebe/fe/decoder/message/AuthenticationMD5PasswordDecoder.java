/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe.decoder.message;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.MessageDecoder;
import com.eightkdata.pgfebe.common.exception.FeBeException;
import com.eightkdata.pgfebe.common.message.AuthenticationMD5Password;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
@Immutable
public class AuthenticationMD5PasswordDecoder implements MessageDecoder<FeBeMessage> {
    private static final int SALT_LENGTH = 4;

    @Override
    public AuthenticationMD5Password decode(@Nonnull ByteBuffer byteBuffer) throws FeBeException {
        byte[] salt = new byte[SALT_LENGTH];
        byteBuffer.get(salt);

        return new AuthenticationMD5Password.Builder()
                .setSalt(salt)
                .build();
    }
}
