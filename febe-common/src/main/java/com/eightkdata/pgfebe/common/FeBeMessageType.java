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

import static com.eightkdata.pgfebe.common.FeBe.*;

/**
 * Created: 21/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum FeBeMessageType {
    AuthenticationOk					(   AUTH_MESSAGE_TYPE,  8,      0                               ),
    AuthenticationKerberosV5            (   AUTH_MESSAGE_TYPE,  8,      2                               ),
    AuthenticationCleartextPassword		(   AUTH_MESSAGE_TYPE,  8,      3                               ),
    AuthenticationMD5Password           (   AUTH_MESSAGE_TYPE,  12,     5                               ),
    AuthenticationSCMCredential         (   AUTH_MESSAGE_TYPE,  8,      6                               ),
    AuthenticationGSS                   (   AUTH_MESSAGE_TYPE,  8,      7                               ),
    AuthenticationSSPI                  (   AUTH_MESSAGE_TYPE,  8,      9                               ),
    AuthenticationGSSContinue           (   AUTH_MESSAGE_TYPE,  null,   8                               ),
    BackendKeyData                      (   'K',                12,     null                            ),
    Bind                                (   'B',                null,   null                            ),
    BindComplete						(   '2',                4,   	null                            ),
    CancelRequest                       (   null,               16,     1234, 5678                      ),
    Close                               (   'C',                null,	null    	                    ),
    CloseComplete						(   '3',                4,      null    	                    ),
    CommandComplete						(   'C',                null,   null    	                    ),
    CopyData							(   'd',                null,   null    	                    ),
    CopyDone							(   'c',                4,   	null    	                    ),
    CopyFail							(   'f',                null,  	null    	                    ),
    CopyInResponse						(   'G',                null,  	null    	                    ),
    CopyOutResponse						(   'H',                null,  	null    	                    ),
    CopyBothResponse					(   'W',                null,  	null    	                    ),
    DataRow								(   'D',                null,  	null    	                    ),
    Describe							(   'D',                null,  	null    	                    ),
    EmptyQueryResponse					(   'I',                4,  	null    	                    ),
    ErrorResponse						(   'E',                null,  	null    	                    ),
    Execute								(   'E',                null,  	null    	                    ),
    Flush								(   'H',                4,  	null    	                    ),
    FunctionCall						(   'F',                null,  	null    	                    ),
    FunctionCallResponse				(   'V',                null,  	null    	                    ),
    NoData								(   'n',                4,  	null    	                    ),
    NoticeResponse						(   'N',                null,  	null    	                    ),
    NotificationResponse				(   'A',                null,  	null    	                    ),
    ParameterDescription				(   't',                null,  	null    	                    ),
    ParameterStatus						(   'S',                null,  	null    	                    ),
    Parse								(   'P',                null,  	null    	                    ),
    ParseComplete						(   '1',                4,  	null    	                    ),
    PasswordMessage						(   'p',                null,  	null    	                    ),
    PortalSuspended						(   's',                4,  	null    	                    ),
    Query								(   'Q',                null,  	null    	                    ),
    ReadyForQuery						(   'Z',                5,  	null    	                    ),
    RowDescription						(   'T',                null,  	null    	                    ),
    SSLRequest							(   null,               8,  	1234, 5679	                    ),
    StartupMessage						(   null,               null,  	PROTOCOL_MAJOR, PROTOCOL_MINOR  ),
    Sync								(   'S',                4,  	null    	                    ),
    Terminate							(   'X',                4,  	null    	                    )
	;

    /**
     * Byte to help identify message types. null for SSLRequest, StartupMessage and CancelRequest messsages
     */
    private final Byte type;

    /**
     * Length in bytes of a fixed-length message. null if length is variable
     */
    private final Integer length;

    /**
     * Field used within that types to distinguish different message variations
     */
    private final Integer subtype;

    /**
     * Cached value for the header length
     */
    private final int headerLength;

    /**
     * Cached value: if the message is self-contained within the header or rather has a payload
     */
    private final boolean hasPayload;

    /**
     * If message type indicates a message with no payload (only header) we store here a singleton of the FeBeMessage
     */
    private final FeBeMessage headerOnlyInstance;

    FeBeMessageType(Character type, Integer length, Integer subtype) {
        Preconditions.checkArgument(null == length || length > 0, "Illegal message length");
        Preconditions.checkArgument(null != type || null != subtype, "Illegal message type/subtype specification");

        this.type = (null == type ? null : (byte) type.charValue());
        this.length = length;
        this.subtype = subtype;
        this.headerLength = computeHeaderLength(type, subtype);
        this.hasPayload = hasFixedLength() && length == headerLength;
        this.headerOnlyInstance = hasPayload ? null : new HeaderOnlyMessage(this);
    }

    FeBeMessageType(Character type, Integer length, int subtype1, int subtype2) {
        this(type, null, ((0x0000ffff & subtype1) << Short.SIZE) | (0x0000ffff & subtype2));
        Preconditions.checkArgument(subtype1 < Short.MAX_VALUE, "Illegal subtype 1");
        Preconditions.checkArgument(subtype2 < Short.MAX_VALUE, "Illegal subtype 2");
    }

    private int computeHeaderLength(Character type, Integer subtype) {
        int headerLength = Ints.BYTES;                      // length field
        if(null != type)    headerLength += 1;              // byte field
        if(null != subtype) headerLength += Ints.BYTES;     // subtype field

        return headerLength;
    }

    public Byte getType() {
        return type;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getSubtype() {
        return subtype;
    }

    public boolean hasFixedLength() {
        return length != null;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public boolean hasPayload() {
        return hasPayload;
    }

    public FeBeMessage getHeaderOnlyInstance() {
        return headerOnlyInstance;
    }
}
