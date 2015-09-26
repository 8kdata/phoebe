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
import java.nio.charset.Charset;

/**
 * Static helper methods useful when writing encoders.
 */
public class EncodingUtil {
    private EncodingUtil() {}

    /**
     * Writes the given String to the ByteBuf as a C-String, i.e., a string terminated by the null byte ('0')
     *
     * @param buf The buffer where to write
     * @param s The string
     * @param encoding Encoding to use to write the string
     */
    public static void writeCString(@Nonnull ByteBuf buf, @Nonnull String s, @Nonnull Charset encoding) {
        if(Charsets.US_ASCII.getCharset() == encoding)
            ByteBufUtil.writeAscii(buf, s);
        else if(Charsets.UTF_8.getCharset() == encoding)
            ByteBufUtil.writeUtf8(buf, s);
        else
            buf.writeBytes(s.getBytes(encoding));

        buf.writeByte(0);
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

    public static int lengthCString(@Nonnull String s, @Nonnull Charset encoding) {
        // TODO: compute the length without producing and copying the byte[] of getBytes, at least for
        // UTF-8 and ASCII/iso-8859-1 encodings

        return s.getBytes(encoding).length + ByteSize.BYTE;     // null ending byte
    }
}
