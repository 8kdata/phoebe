/*
 * Copyright (c) 2015, 8Kdata Technology S.L.
 *
 * Permission to use, copy, modify, and distribute this software and its documentation for any purpose,
 * without fee, and without a written agreement is hereby granted, provided that the above copyright notice and this
 * paragraph and the following two paragraphs appear in all copies.
 *
 * IN NO EVENT SHALL 8Kdata BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF 8Kdata HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 8Kdata SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS,
 * AND 8Kdata HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 */


package com.eightkdata.phoebe.common.util;

import com.google.common.math.DoubleMath;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Convenience methods to help allocate {@link ByteBuf}s
 */
public class ByteBufAllocatorUtil {

    private static @Nonnull ByteBuf allocByteBuf(
            @Nonnull ByteBufAllocator allocator, @Nonnegative int size, @Nonnegative int capacity
    ) {
        checkNotNull(allocator, "allocator");
        checkState(size > 0, "Requested size for the allocated ByteBuf must be non-zero (given = %s)", size);
        assert capacity >= size : "Capacity (" + capacity + ") must be >= size (" + size + ")";

        // We call ioBuffer to return a suitable ByteBuf for I/O.
        // When supported by the underlying, Netty will return a pooled, direct ByteBuf
        return allocator.ioBuffer(
                size,
                // We round capacity to the next power of 2. Netty does that internally anyway, but we try to help here.
                (int) Math.pow(2, DoubleMath.log2(capacity, RoundingMode.CEILING))
        );
    }

    /**
     * Allocates a {@link ByteBuf} suitable to have strings encoded into it.
     * More specifically, it will reserve enough capacity to write strings in any available encoding.
     */
    public static @Nonnull ByteBuf allocStringByteBuf(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnegative int size
    ) {
        // Due to Netty's internal implementation, when storing string data,
        // max capacity should be at least max-bytes-per-char * n_chars
        // That means at least 4 times number of characters.
        return allocByteBuf(byteBufAllocator, size, size * 4);
    }

    /**
     * Allocates a {@link ByteBuf} which won't strings encoded into it.
     * This will create a {@link ByteBuf} with a capacity equal to the requested size.
     */
    public static @Nonnull ByteBuf allocNonStringByteBuf(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnegative int size
    ) {
        return allocByteBuf(byteBufAllocator, size, size);
    }

}
