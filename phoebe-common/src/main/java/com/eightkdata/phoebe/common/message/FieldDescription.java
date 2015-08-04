package com.eightkdata.phoebe.common.message;

/**
 * Created by ianp on 2015-08-04.
 */
public class FieldDescription {
    private final String name;
    private final int tableId;
    private final short columnId;
    private final int typeId;
    private final short typeSize;
    private final int typeModifier;
    private final RowDescription.Format format;

    public FieldDescription(String name, int tableId, short columnId, int typeId, short typeSize, int typeModifier, RowDescription.Format format) {
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

    public RowDescription.Format getFormat() {
        return format;
    }
}
