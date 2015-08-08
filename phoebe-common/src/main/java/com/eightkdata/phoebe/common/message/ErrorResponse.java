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
import com.eightkdata.phoebe.common.MessageType;
import com.google.common.base.MoreObjects;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
public class ErrorResponse extends BaseMessage {

    // todo: the field codes should probably use an enum

    private final Map<Byte, String> fields;

    public ErrorResponse(Map<Byte, String> fields) {
        this.fields = Collections.unmodifiableMap(fields);
    }

    public String getSeverity() {
        return fields.get((byte) 'S');
    }

    public String getCode() {
        return fields.get((byte) 'C');
    }

    public String getMessage() {
        return fields.get((byte) 'M');
    }

    public String getDetail() {
        return fields.get((byte) 'D');
    }

    @Override
    public MessageType getType() {
        return MessageType.ErrorResponse;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        int length = 0;
        for (Map.Entry<Byte, String> field : fields.entrySet()) {
            length += 1 + Encoders.stringLength(field.getValue(), encoding);
        }
        length += 1; // zero byte terminator
        return length;
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add("severity", getSeverity());
        toStringHelper.add("code", getCode());
        toStringHelper.add("message", getMessage());
    }
}
