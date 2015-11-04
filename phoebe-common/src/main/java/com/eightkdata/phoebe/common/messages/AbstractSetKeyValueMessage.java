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


package com.eightkdata.phoebe.common.messages;

import com.eightkdata.phoebe.common.message.AbstractCharsetByteBufMessage;
import com.eightkdata.phoebe.common.util.*;
import com.google.common.base.MoreObjects;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An abstract class that represents a message whose body contains on a set of key-values,
 * where both the key and value are text.
 *
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public abstract class AbstractSetKeyValueMessage extends AbstractCharsetByteBufMessage {

    public AbstractSetKeyValueMessage(@Nonnull ByteBuf byteBuf, @Nonnull Charset charset) {
        super(byteBuf, charset);
    }

    protected static ByteBuf encodeToByteBuf(
            @Nonnull ByteBufAllocator byteBufAllocator, @Nonnull Charset charset,
            @Nonnull Map<String,String> parameters
    ) {
        // Validate input arguments
        checkNotNull(parameters, "parameters");
        checkNotNull(charset, "charset");

        // Encode
        ByteBuf byteBuf = ByteBufAllocatorUtil.allocStringByteBuf(
                byteBufAllocator, computePayloadLength(parameters, charset)
        );

        for(Map.Entry<String,String> param : parameters.entrySet()) {
            if(null != param.getValue()) {
                EncodingUtil.writeCString(byteBuf, param.getKey(), charset);
                EncodingUtil.writeCString(byteBuf, param.getValue(), charset);
            }
        }

        byteBuf.writeByte(0);       // Signal end-of parameter list

        return byteBuf;
    }

    private static int computePayloadLength(@Nonnull Map<String,String> parameters, @Nonnull Charset charset) {
        int length = ByteSize.BYTE;      // ending '0' byte

        for(Map.Entry<String, String> param : parameters.entrySet()) {
            length += EncodingUtil.lengthCString(param.getKey(), charset);
            length += EncodingUtil.lengthCString(param.getValue(), charset);
        }

        return length;
    }

    private static final class PayloadInformationFiller implements KeyValueIterator<CharSequence,CharSequence> {
        private final MoreObjects.ToStringHelper toStringHelper;

        public PayloadInformationFiller(MoreObjects.ToStringHelper toStringHelper) {
            this.toStringHelper = toStringHelper;
        }

        @Override
        public void doWith(@Nonnull CharSequence key, @Nullable CharSequence value) {
            toStringHelper.add(key.toString(), value);
        }
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        iterateParameters(new PayloadInformationFiller(toStringHelper));
    }

    public void iterateParameters(KeyValueIterator<CharSequence,CharSequence> keyValueIterator) {
        assert byteBuf.readableBytes() > 0 : "Empty Startup Message";

        int offset = 0;
        int size = byteBuf.readableBytes() - ByteSize.BYTE;   // '0' end-of-parameters terminating byte

        do {
            CharSequence key = DecodingUtil.getCString(byteBuf, offset, charset);
            offset += (key.length() + ByteSize.BYTE);   // '0' string terminating byte
            CharSequence value = DecodingUtil.getCString(byteBuf, offset, charset);
            offset += (value.length() + ByteSize.BYTE);   // '0' string terminating byte

            keyValueIterator.doWith(key, value);
        } while (offset < size);

    }
}
