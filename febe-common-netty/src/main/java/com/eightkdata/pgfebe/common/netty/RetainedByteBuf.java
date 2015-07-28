/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.netty;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * Created: 28/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class RetainedByteBuf {
    private final ByteBuf buffer;

    public RetainedByteBuf(ByteBuf buffer) {
        this.buffer = buffer.retain();
    }

    public void release() {
        buffer.release();
    }

    public ByteBuffer getNIOBuffer() {
        return buffer.nioBuffer();
    }
}
