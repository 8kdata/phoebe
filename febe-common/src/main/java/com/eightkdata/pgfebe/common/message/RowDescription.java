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

import com.eightkdata.pgfebe.common.Encoders;
import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;

import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

@Immutable
public class RowDescription implements FeBeMessage {

    private final List<Field> fields;

    public RowDescription(List<Field> fields) {
        this.fields = Collections.unmodifiableList(fields);
    }

    public List<Field> getFields() {
        return fields;
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.RowDescription;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        int length = 4; // number of fields
        for (Field field : fields) {
            length += Encoders.stringLength(field.name, encoding);
            length += 18; // non-name field data
        }
        return length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(16 + fields.size() * 16);
        builder.append("RowDescription(");
        for (Field field : fields) { builder.append(field.name).append(","); }
        builder.setLength(builder.length() - 1);
        return builder.append(")").toString();
    }

    public static class Field {
        private final String name;
        private final int tableId;
        private final short columnId;
        private final int typeId;
        private final short typeSize;
        private final int typeModifier;
        private final Format format;

        public Field(String name, int tableId, short columnId, int typeId, short typeSize, int typeModifier, Format format) {
            this.name = name;
            this.tableId = tableId;
            this.columnId = columnId;
            this.typeId = typeId;
            this.typeSize = typeSize;
            this.typeModifier = typeModifier;
            this.format = format;
        }
    }

    public enum Format {
        TEXT, BINARY
    }

}
