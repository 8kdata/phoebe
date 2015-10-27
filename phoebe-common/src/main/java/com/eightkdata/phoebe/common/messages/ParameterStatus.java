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
import com.eightkdata.phoebe.common.message.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

/**
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public class ParameterStatus extends AbstractSetKeyValueMessage {

    public ParameterStatus(@Nonnull ByteBuf byteBuf, @Nonnull Charset charset) {
        super(byteBuf, charset);
    }

    static ParameterStatus encode(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnull Charset charset,
            @Nonnull SessionParameters sessionParameters
    ) {
        return new ParameterStatus(
                encodeToByteBuf(byteBufAllocator, charset, sessionParameters.parametersMap()),
                charset
        );
    }


    @Override
    public MessageType getType() {
        return MessageType.ParameterStatus;
    }

}
