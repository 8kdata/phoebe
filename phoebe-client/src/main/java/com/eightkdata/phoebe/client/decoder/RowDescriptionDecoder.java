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

package com.eightkdata.phoebe.client.decoder;

import com.eightkdata.phoebe.common.Decoders;
import com.eightkdata.phoebe.common.MessageDecoder;
import com.eightkdata.phoebe.common.message.RowDescription;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Decoder for {@link RowDescription} messages.
 */
@Immutable
class RowDescriptionDecoder implements MessageDecoder<RowDescription> {

    @Override
    public RowDescription decode(@Nonnull ByteBuf in, @Nonnull Charset encoding) {
        int numFields = in.readShort();
        List<RowDescription.Field> fields = new ArrayList<RowDescription.Field>(numFields);
        for (int i = 0; i < numFields; ++i) {
            fields.add(decodeField(in, encoding));
        }
        return new RowDescription(fields);
    }

    private RowDescription.Field decodeField(ByteBuf in, Charset encoding) {
        String name = Decoders.readString(in, encoding);
        int tableId = in.readInt();
        short columnId = in.readShort();
        int typeId = in.readInt();
        short typeSize = in.readShort();
        int typeModifier = in.readInt();
        RowDescription.Format format = decodeFormat(in.readShort());
        return new RowDescription.Field(name, tableId, columnId, typeId, typeSize, typeModifier, format);
    }

    private RowDescription.Format decodeFormat(short formatCode) {
        switch (formatCode) {
            case 0: return RowDescription.Format.TEXT;
            case 1: return RowDescription.Format.BINARY;
            default: throw new CorruptedFrameException("unknown field format code: " + formatCode);
        }
    }

}
