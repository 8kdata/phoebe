/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.fe;

import com.eightkdata.pgfebe.common.BeMessageType;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import com.eightkdata.pgfebe.common.netty.NettyMessageParsedHeaderBufferPayload;
import com.eightkdata.pgfebe.common.netty.RetainedByteBuf;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created: 28/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public class BeMessageParsedHeaderBufferPayload extends NettyMessageParsedHeaderBufferPayload {
    private final BeMessageType beMessageType;

    public BeMessageParsedHeaderBufferPayload(
            @Nonnull FeBeMessageType febeMessageType, @Nonnull BeMessageType beMessageType,
            int headerLength, long messageLength, @Nullable RetainedByteBuf retainedByteBuf
    ) {
        super(febeMessageType, headerLength, messageLength, retainedByteBuf);

        Preconditions.checkNotNull(beMessageType);
        Preconditions.checkArgument(beMessageType.getFeBeMessageType() == febeMessageType);
        this.beMessageType = beMessageType;
    }

    public BeMessageType getBeMessageType() {
        return beMessageType;
    }
}
