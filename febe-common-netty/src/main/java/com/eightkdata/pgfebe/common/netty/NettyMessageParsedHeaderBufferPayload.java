/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common.netty;

import com.eightkdata.pgfebe.common.FeBeMessageParsedHeaderBufferPayload;
import com.eightkdata.pgfebe.common.FeBeMessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created: 28/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class NettyMessageParsedHeaderBufferPayload extends FeBeMessageParsedHeaderBufferPayload {
    private final RetainedByteBuf retainedByteBuf;

    public NettyMessageParsedHeaderBufferPayload(
            @Nonnull FeBeMessageType febeMessageType,
            int headerLength, long messageLength, @Nullable RetainedByteBuf retainedByteBuf
    ) {
        super(
                febeMessageType, headerLength, messageLength,
                null == retainedByteBuf ? null : retainedByteBuf.getNIOBuffer()
        );

        this.retainedByteBuf = retainedByteBuf;
    }

    public void releaseRetainedBuffer() {
        if(retainedByteBuf != null) {
            retainedByteBuf.release();
        }
    }
}
