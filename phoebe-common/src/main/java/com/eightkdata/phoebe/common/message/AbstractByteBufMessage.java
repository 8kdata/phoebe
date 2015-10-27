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


package com.eightkdata.phoebe.common.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ReadOnlyByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
@Immutable
public abstract class AbstractByteBufMessage extends AbstractMessage implements ByteBufMessage {

    protected final ByteBuf byteBuf;

    public AbstractByteBufMessage(@Nonnull ByteBuf byteBuf) {
        this.byteBuf = new ReadOnlyByteBuf(checkNotNull(byteBuf.retain()));
    }

    @Override @Nonnull
    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    @Override
    public int size() {
        return byteBuf.readableBytes();
    }

}
