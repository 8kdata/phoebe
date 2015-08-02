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
import io.netty.handler.codec.CorruptedFrameException;

import java.nio.charset.Charset;

/**
 * Static helper methods, needed because Java 6 does not support extension methods.
 */
public class Decoders {

    public static String checkNotNullOrEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            throw new NullPointerException(name);
        }
        return value;
    }

    public static String readString(ByteBuf in, Charset encoding) {
        int length = in.bytesBefore((byte) 0);
        switch (length) {
            case -1: throw new CorruptedFrameException("unterminated string");
            case 0:
                in.readByte(); // discard the trailing zero
                return "";
            default:
                String s = in.readSlice(length).toString(encoding);
                in.readByte(); // discard the trailing zero
                return s;
        }
    }

    private Decoders() {}

}
