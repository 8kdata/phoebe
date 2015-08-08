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


package com.eightkdata.phoebe.common;

import com.eightkdata.phoebe.common.util.ByteSize;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

/**
 * Created: 21/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum MessageType {
    AuthenticationOk                    (   'R',  	8,      0                               			),
    AuthenticationKerberosV5            (   'R',  	8,      2                               			),
    AuthenticationCleartextPassword     (   'R',  	8,      3                               			),
    AuthenticationMD5Password           (   'R',  	12,     5                               			),
    AuthenticationSCMCredential         (   'R',  	8,      6                               			),
    AuthenticationGSS                   (   'R',  	8,      7                               			),
    AuthenticationSSPI                  (   'R',  	8,      9                               			),
    AuthenticationGSSContinue           (   'R',  	null,   8                               			),
    BackendKeyData                      (   'K',    12,     null                            			),
    Bind                                (   'B',    null,   null                            			),
    BindComplete                        (   '2',    4,      null                            			),
    CancelRequest                       (   null,   16,     1234, 5678                      			),
    Close                               (   'C',    null,   null                            			),
    CloseComplete                       (   '3',    4,      null                            			),
    CommandComplete                     (   'C',    null,   null                            			),
    CopyData                            (   'd',    null,   null                            			),
    CopyDone                            (   'c',    4,      null                            			),
    CopyFail                            (   'f',    null,   null                            			),
    CopyInResponse                      (   'G',    null,   null                            			),
    CopyOutResponse                     (   'H',    null,   null                            			),
    CopyBothResponse                    (   'W',    null,   null                            			),
    DataRow                             (   'D',    null,   null                            			),
    Describe                            (   'D',    null,   null                            			),
    EmptyQueryResponse                  (   'I',    4,      null                            			),
    ErrorResponse                       (   'E',    null,   null                            			),
    Execute                             (   'E',    null,   null                            			),
    Flush                               (   'H',    4,      null                            			),
    FunctionCall                        (   'F',    null,   null                            			),
    FunctionCallResponse                (   'V',    null,   null                            			),
    NoData                              (   'n',    4,      null                            			),
    NoticeResponse                      (   'N',    null,   null                            			),
    NotificationResponse                (   'A',    null,   null                            			),
    ParameterDescription                (   't',    null,   null                            			),
    ParameterStatus                     (   'S',    null,   null                            			),
    Parse                               (   'P',    null,   null                            			),
    ParseComplete                       (   '1',    4,      null                            			),
    PasswordMessage                     (   'p',    null,   null                            			),
    PortalSuspended                     (   's',    4,      null                            			),
    Query                               (   'Q',    null,   null                            			),
    ReadyForQuery                       (   'Z',    5,      null                            			),
    RowDescription                      (   'T',    null,   null                            			),
    SSLRequest                          (   null,   8,      1234, 5679                      			),
    StartupMessage                      (   null,   null,   FeBe.PROTOCOL_MAJOR, FeBe.PROTOCOL_MINOR    ),
    Sync                                (   'S',    4,      null                                        ),
    Terminate                           (   'X',    4,      null                                        )
    ;

    public static final byte AUTHENTICATION_TYPE = 'R';

    private static final byte NOT_APPLICABLE = -1;

    private final byte type;
    private final boolean hasType;
    private final int subType;
    private final boolean hasSubType;
    private final int fixedMessageLength;
    private final int fixedTotalMessageLength;
    private final boolean isFixedLengthMessage;
    @Nonnegative private final int headerLength;

    MessageType(@Nullable Character type, @Nullable @Nonnegative Integer length, @Nullable Integer subType) {
        Preconditions.checkArgument(null == length || length > 0, "Illegal message length");

        this.hasType = (null != type);
        this.type = this.hasType ? (byte) type.charValue() : NOT_APPLICABLE;
        this.hasSubType = (null != subType);
        this.subType = this.hasSubType ? subType : NOT_APPLICABLE;

        this.isFixedLengthMessage = (null != length);
        this.fixedMessageLength = this.isFixedLengthMessage ? length : NOT_APPLICABLE;
        this.fixedTotalMessageLength = this.fixedMessageLength + (this.hasType ? ByteSize.BYTE : 0);
        this.headerLength = computeHeaderLength(type, subType);
    }

    MessageType(@Nullable Character type, @Nullable Integer length, int subtype1, int subtype2) {
        this(type, length, ((0x0000ffff & subtype1) << Short.SIZE) | (0x0000ffff & subtype2));

        Preconditions.checkArgument(subtype1 < Short.MAX_VALUE, "Illegal subtype 1");
        Preconditions.checkArgument(subtype2 < Short.MAX_VALUE, "Illegal subtype 2");
    }

    private int computeHeaderLength(Character type, Integer subtype) {
        int headerLength = ByteSize.INTEGER;                        // length field
        if (null != type) { headerLength += ByteSize.BYTE; }        // byte field
        if (null != subtype) { headerLength += ByteSize.INTEGER; }  // subtype field

        return headerLength;
    }

    /**
     * Checks whether this message has a type identifier or not.
     *
     * @return true if the message has a type identifier, false otherwise
     */
    public boolean hasType() {
        return hasType;
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
     * Checks whether this message has a subtype identifier or not.
     *
     * @return true if the message has a subtype identifier, false otherwise
     */
    public boolean hasSubType() {
        return hasSubType;
    }

    /**
     * Some messages (e.g. authentication) can have different subtypes, this field is used to distinguish between them.
     */
    public int getSubtype() {
        return subType;
    }

    /**
     * Whether the message has a pre-defined or variable length
     *
     * @return true if the length of every message of this type is known a priori, independently of the message payload
     */
    public boolean isFixedLengthMessage() {
        return isFixedLengthMessage;
    }

    /**
     * The length in bytes for fixed-length messages, or {@code -1} if the length is variable.
     * This is the length considered by PostgreSQL. It does exclude the byte type, if present.
     */
    public int getFixedMessageLength() {
        return fixedMessageLength;
    }

    /**
     * The total length in bytes for fixed-length messages, or {@code -1} if the length is variable.
     * This is the total length of the message, including the byte type, if present.
     */
    public int getFixedTotalMessageLength() {
        return fixedTotalMessageLength;
    }

    /**
     * The header length, including the byte type, if present
     */
    @Nonnegative
    public int headerLength() {
        return headerLength;
    }

    /**
     * If the message has fixed length, then returns the length of the payload
     *
     * @return the length of the payload for fixed-length messages or {@code -1} for variable-length messages
     */
    public int payloadLength() {
        return isFixedLengthMessage ? fixedTotalMessageLength - headerLength : NOT_APPLICABLE;
    }

    /**
     * Does the message have a payload, or is a header-only message
     */
    public boolean hasPayload() {
        return ! isFixedLengthMessage || fixedTotalMessageLength > headerLength;
    }
}
