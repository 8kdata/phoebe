/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.encoder;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created: 29/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class EncoderUtils {
    public static void encodeToCString(
            @Nonnull String value, @Nonnull ByteBuffer byteBuffer, @Nonnull Charset charset
    ) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(byteBuffer);
        Preconditions.checkNotNull(charset);

        byteBuffer.put(value.getBytes(charset));
        byteBuffer.put((byte) 0);
    }

    public static int computeCStringLength(@Nonnull String value, @Nonnull Charset charset) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(charset);

        return value.getBytes(charset).length + 1;     // '0' byte
    }
}
