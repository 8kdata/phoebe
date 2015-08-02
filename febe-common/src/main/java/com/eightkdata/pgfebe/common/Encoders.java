/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;

/**
 * Static helper methods useful when writing encoders.
 */
public class Encoders {

    public static void writeString(ByteBuf buf, String s, Charset encoding) {
        buf.writeBytes(s.getBytes(encoding)).writeByte(0);
    }

    public static int stringLength(String s, Charset encoding) {
        return s.getBytes(encoding).length + 1;
    }

    public static int computeCStringLength(@Nonnull String value, @Nonnull Charset charset) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(charset);

        return value.getBytes(charset).length + 1;     // '0' byte
    }

}
