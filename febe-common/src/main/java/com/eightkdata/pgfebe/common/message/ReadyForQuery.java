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

package com.eightkdata.pgfebe.common.message;

import com.eightkdata.pgfebe.common.FeBeMessage;
import com.eightkdata.pgfebe.common.FeBeMessageType;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ReadyForQuery implements FeBeMessage {

    private final Status status;

    public ReadyForQuery(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public FeBeMessageType getType() {
        return FeBeMessageType.ReadyForQuery;
    }

    @Override
    public int computePayloadLength() {
        return 1; // 1 status code
    }

    @Override
    public String toString() {
        return "ReadyForQuery(" + status + ")";
    }

    public enum Status {
        IDLE('I'), TRANSACTION('T'), ERROR('E');
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
            throw new IllegalArgumentException("unknown status code: " + ((char) b));
        }
    }

}
