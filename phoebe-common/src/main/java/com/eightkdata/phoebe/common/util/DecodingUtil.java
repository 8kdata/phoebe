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

package com.eightkdata.phoebe.common.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Static helper methods for decoding from {@code ByteBuf}s
 */
public class DecodingUtil {
    private DecodingUtil() {}

    public static CharSequence getCString(@Nonnull ByteBuf buf, @Nonnegative int offset, @Nonnull Charset charset) {
        checkNotNull(buf);
        checkState(offset >= 0);
        checkNotNull(charset);

        int length = buf.bytesBefore(offset, buf.capacity() - offset, (byte) 0);

        switch(length) {
            case -1:    throw new IllegalStateException("No null-byte terminator found in the buffer");
            case 0:     return AsciiString.EMPTY_STRING;
            default:    assert length > 0 : "Length position of a string in a ByteBuffer cannot be negative";
                        return getNonEmptyCString(buf, offset, length, charset);
        }
    }

    private static CharSequence getNonEmptyCString(ByteBuf buf, int offset, int length, Charset charset) {
        // Return a single-byte-per-char AsciiString if charset is single byte and ByteBuf is NIO buffer
        if(
                (Charsets.US_ASCII == charset || Charsets.ISO_8859_1 == charset)
                && buf.nioBufferCount() == 1
        ) {
            return new AsciiString(buf.nioBuffer(), offset, offset + length, false);
        }

        return buf.toString(offset, length, charset);
    }
}
