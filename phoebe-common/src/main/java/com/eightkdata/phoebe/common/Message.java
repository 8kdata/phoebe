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

package com.eightkdata.phoebe.common;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

/**
 * Common interface for all messages.
 */
public interface Message {

    FeBeMessageType getType();

    /**
     * Calculate the amount of storage needed for this messages payload.
     * @param encoding the character set encoding to use for any strings
     * @return the length, in bytes
     */
    int computePayloadLength(Charset encoding);

    
    /**
     * A Decoder that can read a specific message type from an input buffer.
     */
    interface Decoder<T extends Message> {

        /**
         * Read a message from {@code in}.
         *
         * @param in the buffer to read from
         * @param encoding the character set encoding to use
         */
        T decode(@Nonnull ByteBuf in, @Nonnull Charset encoding);

    }


    /**
     * An encoder that can write a specific message type to an output buffer.
     */
    interface Encoder<T extends Message> {

        /**
         * Write {@code message} to {@code out}.
         *
         * @param message the message to encode
         * @param out the buffer to write to
         * @param encoding the character set to use when encoding strings
         */
        void encode(@Nonnull T message, @Nonnull ByteBuf out, @Nonnull Charset encoding);

    }

}
