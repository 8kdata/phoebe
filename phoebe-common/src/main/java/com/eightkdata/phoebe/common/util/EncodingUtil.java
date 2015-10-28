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
import io.netty.buffer.ByteBufUtil;

import javax.annotation.Nonnull;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Static helper methods useful when writing encoders.
 */
public class EncodingUtil {
    private EncodingUtil() {}

    /**
     * Write a C-style (null terminated) string to a buffer.
     *
     * @param buffer the buffer to write to
     * @param s the string
     * @param encoding the encoding to use
     */
    public static void writeCString(@Nonnull ByteBuf buffer, @Nonnull CharSequence s, @Nonnull Charset encoding) {
        if (encoding == Charsets.US_ASCII.getCharset()) {
            ByteBufUtil.writeAscii(buffer, s);
        } else if (encoding == Charsets.UTF_8.getCharset()) {
            ByteBufUtil.writeUtf8(buffer, s);
        } else if (s instanceof String) {
            // xxx: run some tests to see if this is actually faster/smaller than just using encoding.encode()
            buffer.writeBytes(((String) s).getBytes(encoding));
        } else {
            buffer.writeBytes(encoding.encode(CharBuffer.wrap(s)));
        }
        buffer.writeByte(0);
    }

    // message field sizes, these may seem a bit superfluous but they match the data types described in
    // http://www.postgresql.org/docs/9.4/static/protocol-message-types.html#PROTOCOL-MESSAGE-TYPES

    /**
     * The size of an 32-bit integer.
     * @return the size in bytes
     */
    public static int intLength() {
        return 4;
    }

    /**
     * The size of an {@code n}-bit integer.
     * @param n the integer size in bits
     * @return the size in bytes
     */
    public static int intLength(int n) {
        return n / 8;
    }

    /**
     * The size of an array of {@code k} {@code n}-bit integers
     * @param n the integer size in bits
     * @param k the number of integers in the array
     * @return the size in bytes
     */
    public static int intsLength(int n, int k) {
        return (n / 8) * k;

    }

    public static int lengthCString(@Nonnull CharSequence s, @Nonnull Charset cs) {
        // xxx: iff this is slow after profiling, cache the encders in a ThreadLocal<Map>
        CharsetEncoder encoder = cs.newEncoder();
        return encoder.maxBytesPerChar() == 1 ? s.length() : cs.encode(CharBuffer.wrap(s)).remaining();
    }

}
