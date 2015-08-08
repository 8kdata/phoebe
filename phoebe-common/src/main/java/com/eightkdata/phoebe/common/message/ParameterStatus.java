/*
 * Copyright © 2015, 8Kdata Technologies, S.L.
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

import com.eightkdata.phoebe.common.BaseMessage;
import com.eightkdata.phoebe.common.Encoders;
import com.eightkdata.phoebe.common.FeBeMessageType;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.eightkdata.phoebe.common.util.Preconditions.checkTextNotNullNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public final class ParameterStatus extends BaseMessage {
    private final String name;
    private final String value;

    public ParameterStatus(@Nonnull String name, @Nonnull String value) {
        this.name = checkTextNotNullNotEmpty(name, "name");
        this.value = checkNotNull(value, "value");
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.ParameterStatus;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        return Encoders.stringLength(name, encoding) + Encoders.stringLength(value, encoding);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add("name", name);
        toStringHelper.add("value", value);
    }
}
