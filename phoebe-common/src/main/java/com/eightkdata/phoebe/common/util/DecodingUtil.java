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

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.charset.Charset;

import static com.google.common.base.Charsets.ISO_8859_1;
import static com.google.common.base.Charsets.US_ASCII;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Static helper methods for decoding from {@code ByteBuf}s
 */
public class DecodingUtil {
    private DecodingUtil() {}

    public static CharSequence getCString(@Nonnull ByteBuf buffer, @Nonnegative int offset, @Nonnull Charset charset) {
        checkNotNull(buffer, "buffer");
        checkState(offset >= 0, "offset cannot be negative (%s)", offset);
        checkNotNull(charset, "charset");

        int length = buffer.bytesBefore(offset, buffer.capacity() - offset, (byte) 0);

        checkState(length >= 0, "unterminated string found at offset %s", offset);
        return length == 0 ? AsciiString.EMPTY_STRING : getNonEmptyCString(buffer, offset, length, charset);
    }

    /** Return a single-byte-per-char AsciiString if charset is single byte and ByteBuf is NIO buffer. */
    private static CharSequence getNonEmptyCString(ByteBuf buffer, int offset, int length, Charset charset) {
        if ((charset == US_ASCII || charset == ISO_8859_1) && buffer.nioBufferCount() == 1) {
            return new AsciiString(buffer.nioBuffer(), offset, offset + length, false);
        } else {
            return buffer.toString(offset, length, charset);
        }
    }

}
