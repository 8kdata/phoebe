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


package com.eightkdata.phoebe.client.encoder;

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.message.StartupMessage;
import com.eightkdata.phoebe.common.util.KeyValueIterator;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.eightkdata.phoebe.common.Encoders.writeString;

/**
 * Encoder for {@link StartupMessage}s.
 */
@Immutable
class StartupMessageEncoder implements Message.Encoder<StartupMessage> {

    private static class ParametersEncoder implements KeyValueIterator {
        private final ByteBuf byteBuf;

        public ParametersEncoder(ByteBuf byteBuf) {
            this.byteBuf = byteBuf;
        }

        @Override
        public void doWith(@Nonnull String key, @Nullable String value) {
            if(null != value) {
                writeString(byteBuf, key, StartupMessage.FIXED_CHARSET);
                writeString(byteBuf, value, StartupMessage.FIXED_CHARSET);
            }
        }
    }

    @Override
    public void encode(@Nonnull StartupMessage message, @Nonnull final ByteBuf out, @Nonnull Charset encoding) {
        message.iterateParameters(new ParametersEncoder(out));

        out.writeByte(0);       // Signal end-of parameter list
    }
}
