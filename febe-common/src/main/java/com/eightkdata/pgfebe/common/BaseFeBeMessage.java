/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common;

import javax.annotation.Nonnull;

/**
 * Created: 26/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public abstract class BaseFeBeMessage implements FeBeMessage {
    private final FeBeMessageType febeMessageType;

    public BaseFeBeMessage(FeBeMessageType febeMessageType) {
        this.febeMessageType = febeMessageType;
    }

    @Override
    public FeBeMessageType getType() {
        return null;
    }

    private static final String TO_STRING_VALUES_SEPARATOR = ", ";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(febeMessageType.name())
                .append("(")
                .append("length=")
                .append(febeMessageType.getLength());

        return toStringMessagePayload(sb, TO_STRING_VALUES_SEPARATOR)
                .append(")")
                .toString();
    }

    /**
     * toString()-type method to output information about this message's payload.
     * Default implementation does not print any payload information, override to do it.
     * Implementations should output the separator first, and use it consistently to separate various pieces of
     * information.
     * @param sb a StringBuilder on which to output the payload information
     * @return the same StringBuilder received as argument
     */
    protected @Nonnull StringBuilder toStringMessagePayload(@Nonnull StringBuilder sb, @Nonnull String separator) {
        return sb;
    }
}
