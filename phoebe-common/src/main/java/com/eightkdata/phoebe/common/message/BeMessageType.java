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


package com.eightkdata.phoebe.common.message;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created: 25/07/15
 *
 * @author Álvaro Hernández Tortosa <aht@8kdata.com>
 */
public enum BeMessageType {
    AuthenticationOk,
    AuthenticationKerberosV5,
    AuthenticationCleartextPassword,
    AuthenticationMD5Password,
    AuthenticationSCMCredential,
    AuthenticationGSS,
    AuthenticationSSPI,
    AuthenticationGSSContinue,
    BackendKeyData,
    BindComplete,
    CloseComplete,
    CommandComplete,
    CopyData,
    CopyDone,
    CopyInResponse,
    CopyOutResponse,
    CopyBothResponse,
    DataRow,
    EmptyQueryResponse,
    ErrorResponse,
    FunctionCallResponse,
    NoData,
    NoticeResponse,
    NotificationResponse,
    ParameterDescription,
    ParameterStatus,
    ParseComplete,
    PortalSuspended,
    ReadyForQuery,
    RowDescription
    ;

    private final MessageType febeMessageType;

    BeMessageType() {
        this.febeMessageType = MessageType.valueOf(name());
    }

    private static final Map<Byte,BeMessageType> nonAuthMessageTypes = new HashMap<Byte,BeMessageType>();
    private static final Map<Integer,BeMessageType> authMessageTypes = new HashMap<Integer,BeMessageType>();
    public static final int MIN_HEADER_SIZE;
    static {
        int minHeaderSize = Integer.MAX_VALUE;

        for(BeMessageType beMessageType : values()) {
            assert beMessageType.febeMessageType.hasType() : "Backend messages must have a type byte";

            if (beMessageType.getId() == MessageType.AUTHENTICATION_TYPE) {
                assert beMessageType.getSubtype() != null : "Auth be messages must have a subtype";
                authMessageTypes.put(beMessageType.getSubtype(), beMessageType);
            } else {
                assert beMessageType.getSubtype() == null : "Non auth be messages do not have subtype";
                nonAuthMessageTypes.put(beMessageType.getId(), beMessageType);
            }

            if(beMessageType.getHeaderLength() < minHeaderSize) {
                minHeaderSize = beMessageType.getHeaderLength();
            }
        }

        MIN_HEADER_SIZE = minHeaderSize;
    }

    public static @Nullable
    BeMessageType getAuthMessageBySubtype(@Nonnull Integer subtype) {
        checkNotNull(subtype, "subtype");

        return authMessageTypes.get(subtype);
    }

    public static @Nullable
    BeMessageType getNonAuthMessageByType(@Nonnull Byte type) {
        checkNotNull(type, "type");

        return nonAuthMessageTypes.get(type);
    }

    public MessageType getMessageType() {
        return febeMessageType;
    }

    public boolean hasPayload() {
        return febeMessageType.hasPayload();
    }

    public byte getId() {
        return febeMessageType.getType();
    }

    public Integer getSubtype() {
        return febeMessageType.getSubtype();
    }

    public int getHeaderLength() {
        return febeMessageType.headerLength();
    }
}
