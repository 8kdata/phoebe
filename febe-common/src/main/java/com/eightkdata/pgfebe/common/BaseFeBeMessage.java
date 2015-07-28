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
