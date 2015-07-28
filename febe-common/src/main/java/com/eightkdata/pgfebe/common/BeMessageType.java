/*
 * Copyright (c) 2009-2014, 8Kdata Technologies, S.L.
 */

package com.eightkdata.pgfebe.common;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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

    private final FeBeMessageType febeMessageType;

    BeMessageType() {
        this.febeMessageType = FeBeMessageType.valueOf(name());
    }

    private static final Map<Byte,BeMessageType> nonAuthMessageTypes = new HashMap<Byte,BeMessageType>();
    private static final Map<Integer,BeMessageType> authMessageTypes = new HashMap<Integer,BeMessageType>();
    public static final int MIN_HEADER_SIZE;
    static {
        int minHeaderSize = Integer.MAX_VALUE;

        for(BeMessageType beMessageType : values()) {
            assert beMessageType.getType() != null : "Backend messages must have a type byte";

            if(beMessageType.getType() == (byte) FeBe.AUTH_MESSAGE_TYPE.charValue()) {
                assert beMessageType.getSubtype() != null : "Auth be messages must have a subtype";
                authMessageTypes.put(beMessageType.getSubtype(), beMessageType);
            } else {
                assert beMessageType.getSubtype() == null : "Non auth be messages do not have subtype";
                nonAuthMessageTypes.put(beMessageType.getType(), beMessageType);
            }

            if(beMessageType.getHeaderLength() < minHeaderSize) {
                minHeaderSize = beMessageType.getHeaderLength();
            }
        }

        MIN_HEADER_SIZE = minHeaderSize;
    }

    public static @Nullable
    BeMessageType getAuthMessageBySubtype(@Nonnull Integer subtype) {
        Preconditions.checkNotNull(subtype);

        return authMessageTypes.get(subtype);
    }

    public static @Nullable
    BeMessageType getNonAuthMessageByType(@Nonnull Byte type) {
        Preconditions.checkNotNull(type);

        return nonAuthMessageTypes.get(type);
    }

    public FeBeMessageType getFeBeMessageType() {
        return febeMessageType;
    }

    public boolean hasPayload() {
        return febeMessageType.hasPayload();
    }

    public Byte getType() {
        return febeMessageType.getType();
    }

    public Integer getSubtype() {
        return febeMessageType.getSubtype();
    }

    public int getHeaderLength() {
        return febeMessageType.getHeaderLength();
    }
}
