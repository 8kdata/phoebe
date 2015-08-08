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


package com.eightkdata.phoebe.common.message;

import com.eightkdata.phoebe.common.type.TypeFormat;

import javax.annotation.concurrent.Immutable;

@Immutable
public class FieldDescription {

    private final String name;
    private final int tableId;
    private final short columnId;
    private final int typeId;
    private final short typeSize;
    private final int typeModifier;
    private final TypeFormat format;

    public FieldDescription(String name, int tableId, short columnId, int typeId, short typeSize, int typeModifier, TypeFormat format) {
        this.name = name;
        this.tableId = tableId;
        this.columnId = columnId;
        this.typeId = typeId;
        this.typeSize = typeSize;
        this.typeModifier = typeModifier;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public int getTableId() {
        return tableId;
    }

    public short getColumnId() {
        return columnId;
    }

    public int getTypeId() {
        return typeId;
    }

    public short getTypeSize() {
        return typeSize;
    }

    public int getTypeModifier() {
        return typeModifier;
    }

    public TypeFormat getFormat() {
        return format;
    }
}
