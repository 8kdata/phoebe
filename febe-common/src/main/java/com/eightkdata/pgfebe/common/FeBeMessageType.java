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

import com.eightkdata.pgfebe.common.message.HeaderOnlyMessage;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import static com.eightkdata.pgfebe.common.FeBe.PROTOCOL_MAJOR;
import static com.eightkdata.pgfebe.common.FeBe.PROTOCOL_MINOR;

/**
 * Created: 21/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum FeBeMessageType {
    AuthenticationOk                    (   'R',  	8,      0                               ),
    AuthenticationKerberosV5            (   'R',  	8,      2                               ),
    AuthenticationCleartextPassword     (   'R',  	8,      3                               ),
    AuthenticationMD5Password           (   'R',  	12,     5                               ),
    AuthenticationSCMCredential         (   'R',  	8,      6                               ),
    AuthenticationGSS                   (   'R',  	8,      7                               ),
    AuthenticationSSPI                  (   'R',  	8,      9                               ),
    AuthenticationGSSContinue           (   'R',  	null,   8                               ),
    BackendKeyData                      (   'K',    12,     null                            ),
    Bind                                (   'B',    null,   null                            ),
    BindComplete                        (   '2',    4,      null                            ),
    CancelRequest                       (   null,   16,     1234, 5678                      ),
    Close                               (   'C',    null,   null                            ),
    CloseComplete                       (   '3',    4,      null                            ),
    CommandComplete                     (   'C',    null,   null                            ),
    CopyData                            (   'd',    null,   null                            ),
    CopyDone                            (   'c',    4,      null                            ),
    CopyFail                            (   'f',    null,   null                            ),
    CopyInResponse                      (   'G',    null,   null                            ),
    CopyOutResponse                     (   'H',    null,   null                            ),
    CopyBothResponse                    (   'W',    null,   null                            ),
    DataRow                             (   'D',    null,   null                            ),
    Describe                            (   'D',    null,   null                            ),
    EmptyQueryResponse                  (   'I',    4,      null                            ),
    ErrorResponse                       (   'E',    null,   null                            ),
    Execute                             (   'E',    null,   null                            ),
    Flush                               (   'H',    4,      null                            ),
    FunctionCall                        (   'F',    null,   null                            ),
    FunctionCallResponse                (   'V',    null,   null                            ),
    NoData                              (   'n',    4,      null                            ),
    NoticeResponse                      (   'N',    null,   null                            ),
    NotificationResponse                (   'A',    null,   null                            ),
    ParameterDescription                (   't',    null,   null                            ),
    ParameterStatus                     (   'S',    null,   null                            ),
    Parse                               (   'P',    null,   null                            ),
    ParseComplete                       (   '1',    4,      null                            ),
    PasswordMessage                     (   'p',    null,   null                            ),
    PortalSuspended                     (   's',    4,      null                            ),
    Query                               (   'Q',    null,   null                            ),
    ReadyForQuery                       (   'Z',    5,      null                            ),
    RowDescription                      (   'T',    null,   null                            ),
    SSLRequest                          (   null,   8,      1234, 5679                      ),
    StartupMessage                      (   null,   null,   PROTOCOL_MAJOR, PROTOCOL_MINOR  ),
    Sync                                (   'S',    4,      null                            ),
    Terminate                           (   'X',    4,      null                            )
    ;

    public static final byte AUTHENTICATION_TYPE = 'R';

    private static final byte NO_TYPE = -1;

    private final Character charType;
    private final byte type;
    private final Integer length;
    private final Integer subtype;
    private final int headerLength;
    private final boolean hasPayload;
    private final FeBeMessage headerOnlyInstance;

    FeBeMessageType(Character type, Integer length, Integer subtype) {
        Preconditions.checkArgument(null == length || length > 0, "Illegal message length");

        this.charType = type;
        this.type = (null == type) ? NO_TYPE : (byte) type.charValue();
        this.length = length;
        this.subtype = subtype;
        this.headerLength = computeHeaderLength(type, subtype);
        this.hasPayload = (length == null || length > headerLength);
        this.headerOnlyInstance = hasPayload ? null : new HeaderOnlyMessage(this);
    }

    FeBeMessageType(Character type, Integer length, int subtype1, int subtype2) {
        this(type, length, ((0x0000ffff & subtype1) << Short.SIZE) | (0x0000ffff & subtype2));

        Preconditions.checkArgument(subtype1 < Short.MAX_VALUE, "Illegal subtype 1");
        Preconditions.checkArgument(subtype2 < Short.MAX_VALUE, "Illegal subtype 2");
    }

    private int computeHeaderLength(Character type, Integer subtype) {
        int headerLength = Ints.BYTES;                       // length field
        if (null != type) { headerLength += 1; }               // byte field
        if (null != subtype) { headerLength += Ints.BYTES; } // subtype field

        return headerLength;
    }

    /**
     * Get the numeric code used to represent this message type on the wire.
     * Use the method {@link #hasType()} to check if the message has or has not a type.
     * 
     * @return the message id or {@code -1} if the message does not have type.
     */
    public byte getType() {
        return type;
    }

    /**
     * Checks whether this message has a type identifier or not.
     *
     * @return true if the message has a type identifier, false otherwise
     */
    public boolean hasType() {
        return charType != null;
    }

    /**
     * The length in bytes for fixed-length messages, or {@code null} if the length is variable.
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Some messages (e.g. authentication) can have different subtypes, this field is used to distinguish between them.
     */
    public Integer getSubtype() {
        return subtype;
    }

    public boolean hasFixedLength() {
        return length != null;
    }

    /**
     * The header length.
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * Does the message have a variable length payload or not.
     */
    public boolean hasPayload() {
        return hasPayload;
    }

    /**
     * Get the shared instance for header-only messages.
     */
    public FeBeMessage getHeaderOnlyInstance() {
        return headerOnlyInstance;
    }
}
