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

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.message.DataRow;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Decoder for {@link DataRow} messages.
 */
@Immutable
class DataRowDecoder implements Message.Decoder<DataRow> {

    @Override
    public DataRow decode(@Nonnull ByteBuf in, @Nonnull Charset encoding) {
        int numFields = in.readShort();
        List<Object> fields = new ArrayList<Object>(numFields);
        for (int i = 0; i < numFields; ++i) {
            int fieldLength = in.readInt();
            if (fieldLength == -1) {
                fields.add(DataRow.NULL);
            } else {
                fields.add(in.readSlice(fieldLength));
            }
        }
        return new DataRow(fields);
    }

}
