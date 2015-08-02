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

package com.eightkdata.pgfebe.common;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

/**
 * Static helper methods useful when writing encoders.
 */
public class Encoders {

    public static void writeString(@Nonnull ByteBuf buf, @Nonnull String s, @Nonnull Charset encoding) {
        buf.writeBytes(s.getBytes(encoding)).writeByte(0);
    }

    public static int stringLength(@Nonnull String s, @Nonnull Charset encoding) {
        return s.getBytes(encoding).length + 1;
    }

    private Encoders() {}

}
