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
import com.eightkdata.pgfebe.common.message.StartupMessage;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import static com.eightkdata.pgfebe.common.FeBe.PROTOCOL_MAJOR;
import static com.eightkdata.pgfebe.common.FeBe.PROTOCOL_MINOR;
import static com.eightkdata.pgfebe.common.MessageId.*;

/**
 * Created: 21/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum FeBeMessageType {
    AuthenticationOk(AUTHENTICATION, 8, 0),
    AuthenticationKerberosV5(AUTHENTICATION, 8, 2),
    AuthenticationCleartextPassword(AUTHENTICATION, 8, 3),
    AuthenticationMD5Password(AUTHENTICATION, 12, 5),
    AuthenticationSCMCredential(AUTHENTICATION, 8, 6),
    AuthenticationGSS(AUTHENTICATION, 8, 7),
    AuthenticationSSPI(AUTHENTICATION, 8, 9),
    AuthenticationGSSContinue(AUTHENTICATION, null, 8),
    BackendKeyData(BACKEND_KEY_DATA, 12, null),
    Bind(BIND, null, null),
    BindComplete(BIND_COMPLETE, 4, null),
    CancelRequest(NONE, 16, 1234, 5678),
    Close(CLOSE, null, null),
    CloseComplete(CLOSE_COMPLETE, 4, null),
    CommandComplete(COMMAND_COMPLETE, null, null),
    CopyData(COPY_DATA, null, null),
    CopyDone(COPY_DONE, 4, null),
    CopyFail(COPY_FAIL, null, null),
    CopyInResponse(COPY_IN_RESPONSE, null, null),
    CopyOutResponse(COPY_OUT_RESPONSE, null, null),
    CopyBothResponse(COPY_BOTH_RESPONSE, null, null),
    DataRow(DATA_ROW, null, null),
    Describe(DESCRIBE, null, null),
    EmptyQueryResponse(EMPTY_QUERY_RESPONSE, 4, null),
    ErrorResponse(ERROR_RESPONSE, null, null),
    Execute(EXECUTE, null, null),
    Flush(FLUSH, 4, null),
    FunctionCall(FUNCTION_CALL, null, null),
    FunctionCallResponse(FUNCTION_CALL_RESPONSE, null, null),
    NoData(NO_DATA, 4, null),
    NoticeResponse(NOTICE_RESPONSE, null, null),
    NotificationResponse(NOTIFICATION_RESPONSE, null, null),
    ParameterDescription(PARAMETER_DESCRIPTION, null, null),
    ParameterStatus(PARAMETER_STATUS, null, null),
    Parse(PARSE, null, null),
    ParseComplete(PARSE_COMPLETE, 4, null),
    PasswordMessage(PASSWORD_MESSAGE, null, null),
    PortalSuspended(PORTAL_SUSPENDED, 4, null),
    Query(QUERY, null, null),
    ReadyForQuery(READY_FOR_QUERY, 5, null),
    RowDescription(ROW_DESCRIPTION, null, null),
    SSLRequest(NONE, 8, 1234, 5679),
    StartupMessage(NONE, null, PROTOCOL_MAJOR, PROTOCOL_MINOR),
    Sync(SYNC, 4, null),
    Terminate(TERMINATE, 4, null);
    
    private final byte id;

    private final Integer length;

    private final Integer subtype;

    private final int headerLength;

    private final boolean hasPayload;

    private final FeBeMessage headerOnlyInstance;

    FeBeMessageType(byte id, Integer length, Integer subtype) {
        Preconditions.checkArgument(null == length || length > 0, "Illegal message length");

        this.id = id;
        this.length = length;
        this.subtype = subtype;
        this.headerLength = computeHeaderLength(id, subtype);
        this.hasPayload = (length == null || length > headerLength);
        this.headerOnlyInstance = hasPayload ? null : new HeaderOnlyMessage(this);
    }

    FeBeMessageType(byte id, Integer length, int subtype1, int subtype2) {
        this(id, null, ((0x0000ffff & subtype1) << Short.SIZE) | (0x0000ffff & subtype2));
        Preconditions.checkArgument(subtype1 < Short.MAX_VALUE, "Illegal subtype 1");
        Preconditions.checkArgument(subtype2 < Short.MAX_VALUE, "Illegal subtype 2");
    }

    private int computeHeaderLength(byte id, Integer subtype) {
        int headerLength = Ints.BYTES;                       // length field
        if (id != NONE) { headerLength += 1; }               // byte field
        if (subtype != null) { headerLength += Ints.BYTES; } // subtype field
        return headerLength;
    }

    /**
     * Get the numeric code used to represent this message type on the wire.
     * 
     * This will return {@link MessageId#NONE} for {@code SSLRequest}, {@link StartupMessage},
     * and {@code CancelRequest} messsages.
     * 
     * @return the message id.
     */
    public byte getId() {
        return id;
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
