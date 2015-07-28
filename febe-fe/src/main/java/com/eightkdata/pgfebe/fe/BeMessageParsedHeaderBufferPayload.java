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
