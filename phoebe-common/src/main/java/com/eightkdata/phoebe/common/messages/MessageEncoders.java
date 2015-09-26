/*
 * Copyright © 2015, 8Kdata Technology S.L.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for any purpose, without fee, and without
 * a written agreement is hereby granted, provided that the above
 * copyright notice and this paragraph and the following two
 * paragraphs appear in all copies.
 *
 * In no event shall 8Kdata Technology S.L. be liable to any party
 * for direct, indirect, special, incidental, or consequential
 * damages, including lost profits, arising out of the use of this
 * software and its documentation, even if 8Kdata Technology S.L.
 * has been advised of the possibility of such damage.
 *
 * 8Kdata Technology S.L. specifically disclaims any warranties,
 * including, but not limited to, the implied warranties of
 * merchantability and fitness for a particular purpose. the
 * software provided hereunder is on an “as is” basis, and
 * 8Kdata Technology S.L. has no obligations to provide
 * maintenance, support, updates, enhancements, or modifications.
 */


package com.eightkdata.phoebe.common.messages;

import com.eightkdata.phoebe.common.SessionParameters;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class MessageEncoders {

    private final ByteBufAllocator byteBufAllocator;

    public MessageEncoders(@Nonnull ByteBufAllocator byteBufAllocator) {
        this.byteBufAllocator = checkNotNull(byteBufAllocator);
    }

    public @Nonnull AuthenticationMD5Password authenticationMD5Password(@Nonnull byte[] salt) {
        return AuthenticationMD5Password.encode(byteBufAllocator, salt);
    }

    public @Nonnull PasswordMessage passwordMessage(
            @Nonnull Charset charset, @Nonnull CharSequence password
    ) {
        return PasswordMessage.encode(byteBufAllocator, charset, password);
    }

    /**
     * <p>Constructs a StartupMessage.
     * The {@link SessionParameters} need at lest to contain compulsory parameter {@code user}.
     *
     * <p>If not set:
     * <ul>
     *     <li>{@code database}: defaults to the {@code user}</li>
     *     <li>{@code client_encoding}: defaults to {@link StartupMessage.DEFAULT_ENCODING}</li>
     *     <li>{@code lc_messages}: defauls to {@link StartupMessage.DEFAULT_LC_MESSAGES}</li>
     * </ul>
     *
     * @param sessionParameters the session parameters
     */
    public @Nonnull StartupMessage startupMessage(
            @Nonnull Charset charset, @Nonnull SessionParameters sessionParameters
    ) {
        return StartupMessage.encode(byteBufAllocator, charset, sessionParameters);
    }

}
