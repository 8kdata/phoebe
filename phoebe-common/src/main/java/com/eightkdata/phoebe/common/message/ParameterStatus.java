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

import com.eightkdata.phoebe.common.Message;
import com.eightkdata.phoebe.common.FeBeMessageType;

import javax.annotation.concurrent.Immutable;

import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public final class ParameterStatus implements Message {

    private final String name;
    private final String value;

    public ParameterStatus(String name, String value) {
        this.name = checkNotNull(name, "name");
        this.value = checkNotNull(value, "value");
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.ParameterStatus;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ParameterStatus(" + name + "=" + value + ")";
    }
}
