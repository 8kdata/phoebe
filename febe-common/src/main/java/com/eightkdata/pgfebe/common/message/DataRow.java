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

package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * A data row message.
 *
 * This message just holds the raw byte data, it is not interpreted in any
 * way, note also that the byte data is unretained, and so the field data
 * should not be accessed once it has passed through the pipeline.
 */
public class DataRow implements FeBeMessage {

    /**
     * Special marker for {@code null} field values.
     */
    public static final Object NULL = new Object();


    private final List<Object> fields;

    public DataRow(List<Object> fields) {
        this.fields = Collections.unmodifiableList(fields);
    }

    /**
     * An unmodifiable view of the raw field data.
     *
     * The element are either {@link ByteBuf} instances or the special {@link #NULL} marker object.
     * @return the field data.
     */
    public List<Object> getFields() {
        return fields;
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.DataRow;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        int length = 4; // number of fields
        for (Object field : fields) {
            length += 4; // field size
            if (field instanceof ByteBuf) {
                length += ((ByteBuf) field).readableBytes();
            }
        }
        return length;
    }

    @Override
    public String toString() {
        return "DataRow(" + fields.size() + ")";
    }

}
