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
import com.eightkdata.phoebe.common.MessageType;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @see <a href="http://www.postgresql.org/docs/9.4/interactive/protocol-message-formats.html">Message Formats</a>
 */
@Immutable
public class ReadyForQuery extends BaseMessage {
    private final Status status;

    public ReadyForQuery(@Nonnull Status status) {
        this.status = checkNotNull(status);
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public MessageType getType() {
        return MessageType.ReadyForQuery;
    }

    @Override
    public int computePayloadLength(Charset encoding) {
        return Byte.SIZE / 8;   // 1 byte = status code
    }

    @Override
    public void fillInPayloadInformation(MoreObjects.ToStringHelper toStringHelper) {
        toStringHelper.add("status", status.name());
    }

    public enum Status {
        IDLE('I'),
        TRANSACTION('T'),
        ERROR('E');

        final byte code;

        Status(int code) {
            this.code = (byte) code;
        }

        public static Status fromByte(byte b) {
            switch (b) {
                case 'I': return IDLE;
                case 'T': return TRANSACTION;
                case 'E': return ERROR;
            }

            throw new IllegalArgumentException("unknown status code: '" + ((char) b) + "'");
        }
    }
}
